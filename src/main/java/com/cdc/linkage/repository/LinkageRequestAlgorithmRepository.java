package com.cdc.linkage.repository;


import com.cdc.linkage.entities.LinkageRequestAlgorithm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkageRequestAlgorithmRepository extends ReactiveCrudRepository<LinkageRequestAlgorithm, Long> {
}
