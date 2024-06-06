package com.cdc.linkage.model;

import com.cdc.linkage.entities.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonTicket {
  private Person person;
  private int numMatchedInCluster;
  private int recordsCount;
}
