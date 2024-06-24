package com.cdc.linkage.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * LogItem entity.
 * Details: Holds the log messages that were generated during the processing of
 * linkage requests.
 */

@Data
@Table("log_item")
public class LogItem {
  @Id
  private Long id;
  private UUID linkageRequestId;
  private String logger;
  private String logLevel;
  private String trace;
  private String message;
  private LocalDateTime createdTimestamp;

  public LogItem(UUID linkageRequestId, String message) {
    this.createdTimestamp = LocalDateTime.now();
    this.linkageRequestId = linkageRequestId;
    this.message = message;
  }
}
