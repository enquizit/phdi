package com.cdc.linkage.entities;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "algorithm_version")
@Data
@Builder
public class AlgorithmVersion {

  @Id
  private Long id;
  private Long algorithmId;
  private Long versionId;
  private String algorithmJson;
  private LocalDateTime createdAt;



}
