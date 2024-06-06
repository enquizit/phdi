package com.cdc.linkage.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * LinkageRequest entity. Details: Holds the id of linkage request and the id of the algorithm that was used to handle
 * the request.
 */

@Entity
@Table(name = "linkage_request")
@Data
@NoArgsConstructor
public class LinkageRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @OneToMany(mappedBy = "linkageRequest")
  private List<LogItem> logItems;

  @OneToMany(mappedBy = "linkageRequest")
  private List<LinkageRequestAlgorithm> linkageRequest;




}
