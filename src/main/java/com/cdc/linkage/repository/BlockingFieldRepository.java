package com.cdc.linkage.repository;

import com.cdc.linkage.entities.BlockingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockingFieldRepository extends JpaRepository<BlockingField, Long> {
}
