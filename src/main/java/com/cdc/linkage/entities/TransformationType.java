package com.cdc.linkage.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * TransformationType entity.
 * Details: Holds the transformation types that should be applied on the blocking
 * field's value, before using this value to fetch the matching records (E.g.
 * last4: extract that last four characters from the value)
 */


@Table(name = "transformation_type")
@Data
public class TransformationType {

  @Id
  private Long id;
  private String name;

}
