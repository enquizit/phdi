package com.cdc.linkage.repository;

import com.cdc.linkage.entities.AlgorithmParameterMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgorithmParameterMappingRepository extends JpaRepository<AlgorithmParameterMapping, Long> {
}
