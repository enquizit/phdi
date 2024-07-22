package com.cdc.linkage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonTicket {
  private UUID personId;
  private int numMatchedInCluster;
  private int recordsCount;
}
