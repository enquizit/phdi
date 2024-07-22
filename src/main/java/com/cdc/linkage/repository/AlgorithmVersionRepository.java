package com.cdc.linkage.repository;


import com.cdc.linkage.entities.AlgorithmVersion;
import com.cdc.linkage.entities.Patient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AlgorithmVersionRepository extends ReactiveCrudRepository<AlgorithmVersion, Long> {

    @Query("SELECT MAX(version_id) FROM algorithm_version a WHERE a.algorithm_id =:algorithmId")
    Mono<Long> findMaxVersionForAlgorithm(Long algorithmId);

}
