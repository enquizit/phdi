package com.cdc.linkage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmResultDto {
  private Long algorithmId;
  private String algorithmName;
  private String algorithmType;
  private String paramName;
  private String paramValue;
  private String blockFieldName;
  private String blockName;
  private String transformationType;
  private String criteriaFieldName;
  private String functionName;
  private Double criteriaFieldThreshold;
}
