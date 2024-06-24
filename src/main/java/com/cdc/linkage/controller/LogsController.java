package com.cdc.linkage.controller;


import com.cdc.linkage.entities.LogItem;
import com.cdc.linkage.model.DateRangeRequest;
import com.cdc.linkage.model.LogData;
import com.cdc.linkage.repository.LogItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogsController {

  private final LogItemRepository logItemRepository;

  @GetMapping("/logsByRequestId/{reqId}")
  public Flux<LogData> getLogsByRequestId(@PathVariable UUID reqId){
    return logItemRepository.findByLinkageRequestIdOrderByCreatedTimestamp(reqId).map(logItem -> new LogData(
        logItem.getLinkageRequestId(),
        logItem.getMessage(),
        logItem.getCreatedTimestamp()
    ));
  }
  @PostMapping("/logsByDateRange")
  public Flux<LogData> getLogsByDateRange(@RequestBody DateRangeRequest dateRangeRequest) {
    LocalDate start = dateRangeRequest.startDate();
    LocalDate end = dateRangeRequest.endDate().plusDays(1); // Add one day to include the end date
    LocalDateTime startDateTime = start.atStartOfDay();
    LocalDateTime endDateTime = end.atStartOfDay();
    return logItemRepository.findByCreatedTimestampBetweenOrderByCreatedTimestamp(startDateTime, endDateTime).map(logItem -> new LogData(
        logItem.getLinkageRequestId(),
        logItem.getMessage(),
        logItem.getCreatedTimestamp()
    ));
  }


}
