package com.cdc.linkage.repository;

import com.cdc.linkage.entities.LogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogItemRepository extends JpaRepository<LogItem, Long> {
}
