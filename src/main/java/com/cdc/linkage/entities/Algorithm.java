package com.cdc.linkage.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Algorithm entity.
 * Details: Holds the algorithm's id and a user-defined name.
 */

@Entity
@Table(name = "algorithm")
@Data
@NoArgsConstructor
public class Algorithm {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  private String type;

  @OneToMany(mappedBy = "algorithm",fetch = FetchType.LAZY)
  private List<AlgorithmParameterMapping> parameterMappings;

  @OneToMany(mappedBy = "algorithm",fetch = FetchType.LAZY)
  private List<BlockingField> blockingFields;

  @OneToMany(mappedBy = "algorithm",fetch = FetchType.LAZY)
  private List<Criteria> criteriaList;

  @OneToMany(mappedBy = "algorithm", fetch = FetchType.LAZY)
  private List<LinkageRequestAlgorithm> linkageRequests;

  public Algorithm(String name, String type) {
    this.name = name;
    this.type = type;
  }
}
