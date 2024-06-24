package com.cdc.linkage.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


/**
 * Algorithm entity. Details: Holds the algorithm's id and a user-defined name.
 */


@Table(name = "algorithm")
@Data
@NoArgsConstructor
public class Algorithm {
  @Id
  private Long id;
  private String name;
  private String type;

  public Algorithm(String name, String type) {
    this.name = name;
    this.type = type;
  }
}
