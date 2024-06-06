package com.cdc.linkage.service.impl;


import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.entities.AlgorithmParameterMapping;
import com.cdc.linkage.exceptions.AlgorithmNotFoundException;
import com.cdc.linkage.model.CreateAlgorithmRequest;
import com.cdc.linkage.model.ParameterRequest;
import com.cdc.linkage.model.UpdateAlgorithmRequest;
import com.cdc.linkage.repository.AlgorithmParameterMappingRepository;
import com.cdc.linkage.repository.AlgorithmRepository;
import com.cdc.linkage.service.AlgorithmParameterService;
import com.cdc.linkage.service.AlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {

  private final AlgorithmRepository algorithmRepository;
  private final AlgorithmParameterMappingRepository algorithmParameterMappingRepository;
  private final AlgorithmParameterService algorithmParameterService;

  @Transactional
  public Algorithm createAlgorithm(CreateAlgorithmRequest request) {
    Algorithm algorithm = new Algorithm(request.name(), request.type());
    algorithmRepository.save(algorithm);

    for (ParameterRequest paramRequest : request.parameters()) {
      AlgorithmParameter parameter = algorithmParameterService.findOrCreateParameter(paramRequest);
      AlgorithmParameterMapping mapping = new AlgorithmParameterMapping(algorithm, parameter, paramRequest.value());
      algorithmParameterMappingRepository.save(mapping);
    }
    return algorithm;
  }

  @Transactional
  public Algorithm UpdateAlgorithm(UpdateAlgorithmRequest request) {
    Algorithm currentAlgorithm = findAlgorithmById(request.algorithmId());
    if (!(request.algorithmName().equals(currentAlgorithm.getName()))) {
      currentAlgorithm.setName(request.algorithmName());
      algorithmRepository.save(currentAlgorithm);
    }
    for (ParameterRequest paramRequest : request.parameters()) {
      AlgorithmParameterMapping currentMapping = algorithmHaveTheParameter(paramRequest, currentAlgorithm);
      if (currentMapping != null) {
        currentMapping.setParameterValue(paramRequest.value());
        algorithmParameterMappingRepository.save(currentMapping);
      } else {
        AlgorithmParameter parameter = algorithmParameterService.findOrCreateParameter(paramRequest);
        AlgorithmParameterMapping newMapping =
            new AlgorithmParameterMapping(currentAlgorithm, parameter, paramRequest.value());
        algorithmParameterMappingRepository.save(newMapping);
      }
    }
    return currentAlgorithm;
  }

  private AlgorithmParameterMapping algorithmHaveTheParameter(ParameterRequest paramRequest,
      Algorithm currentAlgorithm) {
    for (AlgorithmParameterMapping algorithmParameterMapping : currentAlgorithm.getParameterMappings()) {
      if (algorithmParameterMapping.getAlgorithmParameter().getName().equals(paramRequest.name()))
        return algorithmParameterMapping;
    }
    return null;
  }


  @Override
  public Algorithm findAlgorithmById(long id) {
    return algorithmRepository.findById(id)
        .orElseThrow(() -> new AlgorithmNotFoundException("No algorithm with id =" + id));
  }

  @Override
  public List<Algorithm> findAlgorithmsByIds(List<Long> ids) {
    List<Algorithm> algorithms = algorithmRepository.findAllById(ids);

    // Find IDs that were not found in the database
    List<Long> foundIds = algorithms.stream()
        .map(Algorithm::getId)
        .collect(Collectors.toList());

    List<Long> notFoundIds = ids.stream()
        .filter(id -> !foundIds.contains(id))
        .collect(Collectors.toList());

    // If there are any IDs not found, throw an exception
    if (!notFoundIds.isEmpty()) {
      throw new AlgorithmNotFoundException("No algorithms found with ids = " + notFoundIds);
    }
    return algorithms;
  }



}
