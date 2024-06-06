package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LogItem entity.
 * Details: Holds the log messages that were generated during the processing of
 * linkage requests.
 */

@Entity
@Table(name = "log_item")
@Data
public class LogItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private LinkageRequest linkageRequest;

  private String logger;
  private String logLevel;
  private String trace;
  private String message;
  private LocalDateTime createdTimestamp;

}
