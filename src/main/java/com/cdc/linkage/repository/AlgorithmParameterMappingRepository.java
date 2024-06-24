package com.cdc.linkage.repository;

import com.cdc.linkage.entities.AlgorithmParameterMapping;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AlgorithmParameterMappingRepository extends ReactiveCrudRepository<AlgorithmParameterMapping, Long> {

  Mono<AlgorithmParameterMapping> findByAlgorithmIdAndParameterId(Long algorithmId, Long paramId);
}
