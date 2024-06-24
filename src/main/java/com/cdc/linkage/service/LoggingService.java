package com.cdc.linkage.service;

import com.cdc.linkage.entities.LogItem;
import reactor.core.publisher.Mono;

public interface LoggingService {

  Mono<LogItem> saveLog(LogItem logItem);
}
