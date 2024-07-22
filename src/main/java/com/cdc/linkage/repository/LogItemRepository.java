
package com.cdc.linkage.repository;

import com.cdc.linkage.entities.LogItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface LogItemRepository extends ReactiveCrudRepository<LogItem, Long> {

  Flux<LogItem> findByLinkageRequestIdOrderByCreatedTimestamp(UUID requestId);

  Flux<LogItem> findByCreatedTimestampBetweenOrderByCreatedTimestamp(LocalDateTime startDate, LocalDateTime endDate);
}


