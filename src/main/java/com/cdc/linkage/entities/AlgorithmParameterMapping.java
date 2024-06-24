package com.cdc.linkage.entities;



import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;



@Table(name = "algorithm_parameter_mapping")
@Data
@NoArgsConstructor
public class AlgorithmParameterMapping {

  @Id
  private Long id;
  private Long algorithmId;
  private Long parameterId;
  private String parameterValue;

  public AlgorithmParameterMapping(Long algorithmId, Long parameterId, String parameterValue) {
    this.algorithmId = algorithmId;
    this.parameterId = parameterId;
    this.parameterValue = parameterValue;
  }
}
