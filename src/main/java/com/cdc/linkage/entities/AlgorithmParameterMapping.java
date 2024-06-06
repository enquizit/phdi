package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "algorithm_parameter_mapping")
@Data
@NoArgsConstructor
public class AlgorithmParameterMapping {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "algorithm_id")
  private Algorithm algorithm;

  @ManyToOne
  @JoinColumn(name = "parameter_id")
  private AlgorithmParameter algorithmParameter;

  private String parameterValue;

  public AlgorithmParameterMapping(Algorithm algorithm, AlgorithmParameter algorithmParameter, String parameterValue) {
    this.algorithm = algorithm;
    this.algorithmParameter = algorithmParameter;
    this.parameterValue = parameterValue;
  }
}
