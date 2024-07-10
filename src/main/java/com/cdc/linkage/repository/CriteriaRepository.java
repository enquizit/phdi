package com.cdc.linkage.repository;

import com.cdc.linkage.entities.Criteria;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CriteriaRepository extends ReactiveCrudRepository<Criteria, Long> {

  Mono<Criteria> findByAlgorithmIdAndFieldId(Long algorithmId ,Long fieldId);
}
