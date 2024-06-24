package com.cdc.linkage.repository;

import com.cdc.linkage.entities.Field;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;



@Repository
public interface FieldRepository extends ReactiveCrudRepository<Field, Long> {
  Mono<Field> findByName(String name);
}
