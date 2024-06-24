package com.cdc.linkage.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@Table(name = "person")
@Data
@NoArgsConstructor
public class Person {

  @Id
  private UUID id;
}
