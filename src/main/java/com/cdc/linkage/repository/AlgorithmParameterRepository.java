package com.cdc.linkage.repository;


import com.cdc.linkage.entities.AlgorithmParameter;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;



@Repository
public interface AlgorithmParameterRepository extends ReactiveCrudRepository<AlgorithmParameter, Long> {
  Mono<AlgorithmParameter> findByName(String name);
}
