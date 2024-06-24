package com.cdc.linkage.service.impl;


import com.cdc.linkage.entities.Criteria;
import com.cdc.linkage.entities.Field;
import com.cdc.linkage.exceptions.AlgorithmNotFoundException;
import com.cdc.linkage.model.CreateCriteriaRequest;
import com.cdc.linkage.model.CriteriaFunction;
import com.cdc.linkage.repository.AlgorithmRepository;
import com.cdc.linkage.repository.CriteriaRepository;
import com.cdc.linkage.repository.FieldRepository;
import com.cdc.linkage.service.AlgorithmService;
import com.cdc.linkage.service.CriteriaService;
import com.cdc.linkage.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CriteriaServiceImpl implements CriteriaService {

  private final CriteriaRepository criteriaRepository;
  private final AlgorithmRepository algorithmRepository;
  private final FieldService fieldService;
  private final AlgorithmService algorithmService;
  private final FieldRepository fieldRepository;




  public Mono<Void> createCriteria(CreateCriteriaRequest request) {
    return algorithmRepository.findById(request.algorithmId())
        .switchIfEmpty(Mono.error(new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId())))
        .flatMap(algorithm -> {
          return Flux.fromIterable(request.CriteriaFunctions())
              .flatMap(criteriaFunction ->
                  fieldService.findOrCreateField(criteriaFunction.fieldName())
                      .map(field -> new Criteria(algorithm.getId(), field.getId(), criteriaFunction.functionName()))
              )
              .collectList();
        })
        .flatMapMany(criteriaListToSave ->
            criteriaRepository.saveAll(criteriaListToSave)
        )
        .then() ;
  }

  @Override
  public Mono<Void> updateCriteria(CreateCriteriaRequest request) {
    return algorithmRepository.findById(request.algorithmId())  // Find algorithm by ID reactively
        .flatMap(algorithm -> updateCriteriaFields(algorithm.getId(), request.CriteriaFunctions())).then();
  }

  private Mono<Void> updateCriteriaFields(Long algorithmId, List<CriteriaFunction> criteriaFunctions) {
    List<Mono<Criteria>> saveMappings = criteriaFunctions.stream()
        .map(criteriaFunction ->
            algorithmHaveTheCriteria(algorithmId, criteriaFunction)
                .flatMap(fieldId -> {
                  if (fieldId != 0l) {
                    return criteriaRepository.findByAlgorithmIdAndFieldId(algorithmId, fieldId)
                        .flatMap(currentCriteria -> {
                          currentCriteria.setFunctionName(criteriaFunction.functionName());
                          return criteriaRepository.save(currentCriteria);
                        });
                  } else {
                    return fieldService.findOrCreateField(criteriaFunction.fieldName())
                        .flatMap(field -> {
                          Criteria newCriteria = new Criteria(
                              algorithmId, field.getId(), criteriaFunction.functionName()
                          );
                          return criteriaRepository.save(newCriteria);
                        });
                  }
                })
        )
        .toList();
    return Flux.merge(saveMappings).then();
  }


  private Mono<Long> algorithmHaveTheCriteria(Long algorithmId, CriteriaFunction criteriaFunction) {
    return algorithmService.getAlgorithmDataById(algorithmId)
        .flatMap(algorithmData -> {
          // Check if the field exists in the Blocks
          boolean criteriaExists = algorithmData.getFuncs().containsKey(criteriaFunction.fieldName());
          if (criteriaExists) {
            return fieldRepository.findByName(criteriaFunction.fieldName())
                .map(Field::getId)  //  Return field ID if found
                .defaultIfEmpty(0L);  // Return 0 if the field is not found
          } else {
            return Mono.just(0L); // Return 0 if the v is not found
          }
        });
  }


}
