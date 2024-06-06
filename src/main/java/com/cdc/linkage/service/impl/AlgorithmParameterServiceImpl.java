package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.model.ParameterRequest;
import com.cdc.linkage.repository.AlgorithmParameterRepository;
import com.cdc.linkage.service.AlgorithmParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlgorithmParameterServiceImpl  implements AlgorithmParameterService {

  private final AlgorithmParameterRepository algorithmParameterRepository;

  @Override
  public AlgorithmParameter findOrCreateParameter(ParameterRequest paramRequest ) {
   return algorithmParameterRepository.findByName(paramRequest.name())
        .orElseGet(() -> {
          AlgorithmParameter newParam = new AlgorithmParameter();
          newParam.setName(paramRequest.name());
          newParam.setValue(paramRequest.value()); // Set the default value if needed
          algorithmParameterRepository.save(newParam);
          return newParam;
        });
  }
}
