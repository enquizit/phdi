package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BlockingField entity.
 * Details: Holds the blocking fields that will be used in the algorithm to fetch the
 * matching records from the database with their transformation types.
 */

@Entity
@Table(name = "blocking_field")
@Data
@NoArgsConstructor
public class BlockingField {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "algorithm_id")
  private Algorithm algorithm;

  @ManyToOne
  @JoinColumn(name = "transformation_type")
  private TransformationType transformationType;

  @ManyToOne
  @JoinColumn(name = "field_id")
  private Field field;

  public BlockingField(Algorithm algorithm,Field field, TransformationType transformationType ) {
    this.algorithm = algorithm;
    this.field = field;
    this.transformationType = transformationType;
  }
}
