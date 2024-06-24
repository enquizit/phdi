package com.cdc.linkage.entities;



import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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


@Table(name = "algorithm_parameter")
@Data
public class AlgorithmParameter {

  @Id
  private Long id;
  private String name;
  private String value; //  might be used for some default value
}
