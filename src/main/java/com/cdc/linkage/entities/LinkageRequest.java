package com.cdc.linkage.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


import java.util.UUID;

/**
 * LinkageRequest entity. Details: Holds the id of linkage request and the id of the algorithm that was used to handle
 * the request.
 */


@Table(name = "linkage_request")
@Data
@NoArgsConstructor
public class LinkageRequest {
  @Id
  private UUID id;
}
