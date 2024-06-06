package com.cdc.linkage.repository;


import com.cdc.linkage.entities.LinkageRequestAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkageRequestAlgorithmRepository  extends JpaRepository<LinkageRequestAlgorithm, Long> {
}
