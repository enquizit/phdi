package com.cdc.linkage.repository;


import com.cdc.linkage.entities.AlgorithmParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlgorithmParameterRepository extends JpaRepository<AlgorithmParameter, Long> {
  Optional<AlgorithmParameter> findByName(String name);
}
