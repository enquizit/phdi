
package com.cdc.linkage.repository;

import com.cdc.linkage.entities.FieldScore;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldScoreRepository extends ReactiveCrudRepository<FieldScore, Long> {

}


