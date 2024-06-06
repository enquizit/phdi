package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
/**
 * TransformationType entity.
 * Details: Holds the transformation types that should be applied on the blocking
 * field's value, before using this value to fetch the matching records (E.g.
 * last4: extract that last four characters from the value)
 */

@Entity
@Table(name = "transformation_type")
@Data
public class TransformationType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "transformationType")
  private List<BlockingField> blockingFields;

}
