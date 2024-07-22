package com.cdc.linkage.repository;

import com.cdc.linkage.entities.BlockingField;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BlockingFieldRepository extends ReactiveCrudRepository<BlockingField, Long> {

  Mono<BlockingField> findByAlgorithmIdAndFieldId(Long algorithmId, Long fieldId);
}
