package com.cdc.linkage.service;



import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.CreateAlgorithmRequest;
import com.cdc.linkage.model.UpdateAlgorithmRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AlgorithmService {

  Mono<ResponseEntity<Void>>  createAlgorithm(CreateAlgorithmRequest request);

  Mono<AlgorithmData> getAlgorithmDataById(Long id);

  Mono<ResponseEntity<Void>> updateAlgorithm(UpdateAlgorithmRequest request);

  Flux<Algorithm> findAlgorithmsByIds(List<Long> ids);

}
