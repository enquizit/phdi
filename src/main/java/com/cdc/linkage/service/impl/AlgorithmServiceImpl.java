package com.cdc.linkage.service.impl;


import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.entities.AlgorithmParameterMapping;
import com.cdc.linkage.exceptions.AlgorithmNotFoundException;
import com.cdc.linkage.model.*;
import com.cdc.linkage.repository.AlgorithmParameterMappingRepository;
import com.cdc.linkage.repository.AlgorithmParameterRepository;
import com.cdc.linkage.repository.AlgorithmRepository;
import com.cdc.linkage.service.AlgorithmParameterService;
import com.cdc.linkage.service.AlgorithmService;
import com.cdc.linkage.service.AlgorithmVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import static com.cdc.linkage.utils.StringUtil.isBlankOrNull;



@Service
@RequiredArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

  private final AlgorithmRepository algorithmRepository;
  private final AlgorithmParameterService algorithmParameterService;
  private final AlgorithmParameterMappingRepository algorithmParameterMappingRepository;
  private final AlgorithmParameterRepository algorithmParameterRepository;
  private final  AlgorithmVersionService algorithmVersionService;

  @Override
  public Mono<ResponseEntity<Void>> createAlgorithm(CreateAlgorithmRequest request) {
      boolean isValid = validateCreateAlgorithmRequest(request);
      if (!isValid) {
          return Mono.just(ResponseEntity.badRequest().build());
      }

      Algorithm algorithm = new Algorithm(request.name(), request.type());
      return algorithmRepository.save(algorithm)
              .flatMap(savedAlgorithm -> saveParameterMappings(savedAlgorithm.getId(), request.parameters())
                      .then(getAlgorithmDataById(savedAlgorithm.getId())
                              .flatMap(algorithmVersionService::saveAlgorithmVersion))
              )
              .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
  }


  private Mono<Void> saveParameterMappings(Long algorithmId, List<ParameterRequest> parameterRequests) {
    List<Mono<AlgorithmParameterMapping>> saveMappings = parameterRequests.stream()
        .map(paramRequest ->
            algorithmParameterService.findOrCreateParameter(paramRequest)
                .flatMap(parameter -> {
                  AlgorithmParameterMapping mapping =
                      new AlgorithmParameterMapping(algorithmId, parameter.getId(), paramRequest.value());
                  return algorithmParameterMappingRepository.save(mapping);
                })
        )
        .toList();

    return Flux.merge(saveMappings).then();
  }

  @Override
  public Mono<ResponseEntity<Void>> updateAlgorithm(UpdateAlgorithmRequest request) {
    boolean isValid = validateUpdateAlgorithmRequest(request);
    if (!isValid) {
      return Mono.just(ResponseEntity.badRequest().build());
    }
    return algorithmRepository.findById(request.algorithmId())  // Find algorithm by ID reactively
        .flatMap(currentAlgorithm -> {
          currentAlgorithm.setName(request.algorithmName());
          return algorithmRepository.save(currentAlgorithm)
              .flatMap(savedAlgorithm -> updateParameterMappings(savedAlgorithm.getId(), request.parameters())
                      .then(getAlgorithmDataById(savedAlgorithm.getId())
                              .flatMap(algorithmVersionService::saveAlgorithmVersion)))
              .then(Mono.just(ResponseEntity.status(HttpStatus.OK).build()));
        });
  }

  private Mono<Void> updateParameterMappings(Long algorithmId, List<ParameterRequest> parameterRequests) {
    List<Mono<AlgorithmParameterMapping>> saveMappings = parameterRequests.stream()
        .map(paramRequest ->
            algorithmHaveTheParameter(algorithmId, paramRequest)
                .flatMap(paramId -> {
                  if (paramId != 0l) {
                    return algorithmParameterMappingRepository.findByAlgorithmIdAndParameterId(algorithmId, paramId)
                        .flatMap(currentMapping -> {
                          currentMapping.setParameterValue(paramRequest.value());
                          return algorithmParameterMappingRepository.save(currentMapping);
                        });
                  } else {
                    return algorithmParameterService.findOrCreateParameter(paramRequest)
                        .flatMap(parameter -> {
                          AlgorithmParameterMapping mapping = new AlgorithmParameterMapping(
                              algorithmId, parameter.getId(), paramRequest.value()
                          );
                          return algorithmParameterMappingRepository.save(mapping);
                        });
                  }
                })
        )
        .toList();
    return Flux.merge(saveMappings).then();
  }


  private Mono<Long> algorithmHaveTheParameter(Long algorithmId, ParameterRequest paramRequest) {
    return getAlgorithmDataById(algorithmId)
        .flatMap(algorithmData -> {
          // Check if the parameter exists in the params
          boolean paramExists = algorithmData.getParams().containsKey(paramRequest.name());
          if (paramExists) {
            return algorithmParameterRepository.findByName(paramRequest.name())
                .map(AlgorithmParameter::getId)  //  Return parameter ID if found
                .defaultIfEmpty(0L);  // Return 0 if the parameter is not found
          } else {
            return Mono.just(0L); // Return 0 if the parameter is not found
          }
        });
  }

  @Override
  public Mono<AlgorithmData> getAlgorithmDataById(Long id) {
    return algorithmRepository.getAlgorithmDataArrayById(id)
        .collectList() // Collect all rows into a List
        .map(this::aggregateData); // Aggregate the list into a single AlgorithmData object
  }


  private AlgorithmData aggregateData(List<AlgorithmResultDto> rows) {
    if (rows.isEmpty()) {
      return new AlgorithmData();
    }
    return new AlgorithmData(rows);
  }


  @Override
  public Flux<Algorithm> findAlgorithmsByIds(List<Long> ids) {
    return algorithmRepository.findAllById(ids)
        .collectMap(Algorithm::getId, algorithm -> algorithm)
        .flatMapMany(foundAlgorithmsMap -> {
          List<Long> foundIds = new ArrayList<>(foundAlgorithmsMap.keySet());
          List<Long> notFoundIds = ids.stream()
              .filter(id -> !foundIds.contains(id))
              .toList();

          if (!notFoundIds.isEmpty()) {
            return Mono.error(new AlgorithmNotFoundException("No algorithms found with ids = " + notFoundIds));
          }

          return Flux.fromIterable(foundAlgorithmsMap.values());
        });
  }

  private boolean validateCreateAlgorithmRequest(CreateAlgorithmRequest request) {
    if (isBlankOrNull(request.name()) || isBlankOrNull(request.type()))
      return false;
    return !(request.parameters().stream()
        .anyMatch(param -> isBlankOrNull(param.name()) || isBlankOrNull(param.value())));
  }

  private boolean validateUpdateAlgorithmRequest(UpdateAlgorithmRequest request) {
    if (request.algorithmId() == null || request.algorithmId() == 0 || isBlankOrNull(
        request.algorithmName()))
      return false;
    return !(request.parameters().stream()
        .anyMatch(param -> isBlankOrNull(param.name()) || isBlankOrNull(param.value())));
  }


}
