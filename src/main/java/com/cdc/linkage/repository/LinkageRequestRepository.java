package com.cdc.linkage.repository;

import com.cdc.linkage.entities.LinkageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkageRequestRepository extends JpaRepository<LinkageRequest, Long> {
}
