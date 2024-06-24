package com.cdc.linkage.service;



import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.CreateAlgorithmRequest;
import com.cdc.linkage.model.UpdateAlgorithmRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AlgorithmService {

  Mono<Void> createAlgorithm(CreateAlgorithmRequest request);

  Mono<AlgorithmData> getAlgorithmDataById(Long id);

  Mono<Void> updateAlgorithm(UpdateAlgorithmRequest request);

  Flux<Algorithm> findAlgorithmsByIds(List<Long> ids);

}
