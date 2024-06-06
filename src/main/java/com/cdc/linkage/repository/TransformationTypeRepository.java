package com.cdc.linkage.repository;

import com.cdc.linkage.entities.TransformationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransformationTypeRepository extends JpaRepository<TransformationType, Long> {
}
