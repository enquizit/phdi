package com.cdc.linkage.service;

import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.model.ParameterRequest;
import reactor.core.publisher.Mono;

public interface AlgorithmParameterService {
   Mono<AlgorithmParameter> findOrCreateParameter(ParameterRequest paramRequest);
}
