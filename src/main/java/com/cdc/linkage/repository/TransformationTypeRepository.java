package com.cdc.linkage.repository;

import com.cdc.linkage.entities.TransformationType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransformationTypeRepository extends ReactiveCrudRepository<TransformationType, Long> {
}
