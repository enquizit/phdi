package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.model.ParameterRequest;
import com.cdc.linkage.repository.AlgorithmParameterRepository;
import com.cdc.linkage.service.AlgorithmParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AlgorithmParameterServiceImpl implements AlgorithmParameterService {

  private final AlgorithmParameterRepository algorithmParameterRepository;

  @Override
  public Mono<AlgorithmParameter> findOrCreateParameter(ParameterRequest paramRequest) {
    return algorithmParameterRepository.findByName(paramRequest.name())
        .switchIfEmpty(Mono.defer(() -> {
          AlgorithmParameter newParam = new AlgorithmParameter();
          newParam.setName(paramRequest.name());
          newParam.setValue(paramRequest.value());
          return algorithmParameterRepository.save(newParam);
        }));
  }
}
