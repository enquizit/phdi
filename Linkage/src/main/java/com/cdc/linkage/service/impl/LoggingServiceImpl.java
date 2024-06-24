package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.LogItem;
import com.cdc.linkage.repository.LogItemRepository;
import com.cdc.linkage.service.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {

  private final LogItemRepository logItemRepository;

  @Override
  public Mono<LogItem> saveLog(LogItem logItem) {
   return logItemRepository.save(logItem);
  }
}
