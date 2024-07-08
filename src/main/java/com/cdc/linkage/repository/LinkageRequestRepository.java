package com.cdc.linkage.repository;

import com.cdc.linkage.entities.LinkageRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LinkageRequestRepository extends ReactiveCrudRepository<LinkageRequest, UUID> {
}
