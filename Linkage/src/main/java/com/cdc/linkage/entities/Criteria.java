package com.cdc.linkage.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Criteria entity. Details: Holds the criteria fields with their functions that would be used in the matching process.
 */

@Table(name = "criteria")
@NoArgsConstructor
@Data
public class Criteria {

  @Id
  private Long id;
  private Long algorithmId;
  private Long fieldId;
  private String functionName;

  public Criteria(Long algorithmId, Long fieldId, String functionName) {
    this.algorithmId = algorithmId;
    this.fieldId = fieldId;
    this.functionName = functionName;
  }
}
