package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * AlgorithmParameter entity.
 * Details: Holds the algorithm parameters:
 * similarity_measure
 * threshold
 * true_match_threshold
 * log_odds
 * cluster_ratio
 * matching_rule
 * And any other parameters we might add in the future.
 */

@Entity
@Table(name = "algorithm_parameter")
@Data
public class AlgorithmParameter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String value; //  might be used for some default value

  @OneToMany(mappedBy = "algorithmParameter")
  private List<AlgorithmParameterMapping> parameterMappings;

}
