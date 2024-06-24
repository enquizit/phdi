package com.cdc.linkage.entities;



import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


/**
 * Field entity.
 * Details: Holds all the fields used in an algorithm regardless if they are criteria or
 * blocking fields. This table is referenced from the "criteria" and
 * "blocking_field" tables to avoid duplicate field names.
 */


@Table(name = "field")
@Data
@NoArgsConstructor
public class Field {

  @Id
  private Long id;
  private String name;

  public Field(String name) {
    this.name = name;
  }
}
