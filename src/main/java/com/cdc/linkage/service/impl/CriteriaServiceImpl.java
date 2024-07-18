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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.cdc.linkage.utils.StringUtil.isBlankOrNull;



@Service
@RequiredArgsConstructor
public class CriteriaServiceImpl implements CriteriaService {

  private final CriteriaRepository criteriaRepository;
  private final AlgorithmRepository algorithmRepository;
  private final FieldService fieldService;
  private final AlgorithmService algorithmService;
  private final FieldRepository fieldRepository;



  public Mono<ResponseEntity<Void>> createCriteria(CreateCriteriaRequest request) {
    boolean isValid = validateCreateCriteriaRequest(request);
    if (!isValid) {
      return Mono.just(ResponseEntity.badRequest().build());
    }

    return algorithmRepository.findById(request.algorithmId())
        .switchIfEmpty(Mono.error(new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId())))
        .flatMap(algorithm -> {
          return Flux.fromIterable(request.CriteriaFunctions())
              .flatMap(criteriaFunction ->
                  fieldService.findOrCreateField(criteriaFunction.fieldName())
                      .map(field -> new Criteria(algorithm.getId(), field.getId(), criteriaFunction))
              )
              .collectList();
        })
        .flatMapMany(criteriaListToSave ->

            criteriaRepository.saveAll(criteriaListToSave)
        )
        .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
  }

  @Override
  public Mono<ResponseEntity<Void>> updateCriteria(CreateCriteriaRequest request) {
    boolean isValid = validateCreateCriteriaRequest(request);
    if (!isValid) {
      return Mono.just(ResponseEntity.badRequest().build());
    }
    return algorithmRepository.findById(request.algorithmId())  // Find algorithm by ID reactively
        .flatMap(algorithm -> updateCriteriaFields(algorithm.getId(), request.CriteriaFunctions()))
        .then(Mono.just(ResponseEntity.status(HttpStatus.OK).build()));
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
                          currentCriteria.setThreshold(criteriaFunction.threshold());
                          return criteriaRepository.save(currentCriteria);
                        });
                  } else {
                    return fieldService.findOrCreateField(criteriaFunction.fieldName())
                        .flatMap(field -> {
                          Criteria newCriteria = new Criteria(
                              algorithmId, field.getId(), criteriaFunction);
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

  private boolean validateCreateCriteriaRequest(CreateCriteriaRequest request) {
    if (request.algorithmId() == null || request.algorithmId() == 0)
      return false;
    return !(request.CriteriaFunctions().stream()
        .anyMatch(function -> isBlankOrNull(function.fieldName())
            || isBlankOrNull(function.functionName())
            ||function.threshold() == null));
  }

}
