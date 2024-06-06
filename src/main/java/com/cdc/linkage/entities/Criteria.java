package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Criteria entity.
 * Details: Holds the criteria fields with their functions that would be used in the matching process.
 */

@Entity
@Table(name = "criteria")
@NoArgsConstructor
@Data
public class Criteria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "algorithm_id")
  private Algorithm algorithm;

  @ManyToOne
  @JoinColumn(name = "field_id")
  private Field field;

  private String functionName;

  public Criteria(Algorithm algorithm, Field field, String functionName) {
    this.algorithm = algorithm;
    this.field = field;
    this.functionName = functionName;
  }
}
