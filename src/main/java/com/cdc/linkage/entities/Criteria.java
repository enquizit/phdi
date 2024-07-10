package com.cdc.linkage.entities;

import com.cdc.linkage.model.CriteriaFunction;
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
  private Double threshold;

  public Criteria(Long algorithmId, Long fieldId, CriteriaFunction criteriaFunction) {
    this.algorithmId = algorithmId;
    this.fieldId = fieldId;
    this.functionName = criteriaFunction.functionName();
    this.threshold = criteriaFunction.threshold();
  }
}
