package com.cdc.linkage.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@Table(name = "linkage_request_algorithm")
@Data
@NoArgsConstructor
public class LinkageRequestAlgorithm {

  @Id
  private UUID id;
  private Long algorithmId;
  private UUID linkageRequestId;

  public LinkageRequestAlgorithm(Long algorithmId, UUID linkageRequestId) {
    this.algorithmId = algorithmId;
    this.linkageRequestId = linkageRequestId;
  }
}
