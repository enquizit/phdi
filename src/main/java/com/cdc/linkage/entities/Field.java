package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Field entity.
 * Details: Holds all the fields used in an algorithm regardless if they are criteria or
 * blocking fields. This table is referenced from the "criteria" and
 * "blocking_field" tables to avoid duplicate field names.
 */

@Entity
@Table(name = "field")
@Data
public class Field {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "field")
  private List<BlockingField> blockingFields;

  @OneToMany(mappedBy = "field")
  private List<Criteria> criteriaList;

}
