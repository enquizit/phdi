package com.cdc.linkage.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "linkage_request_algorithm")
@Data
@NoArgsConstructor
public class LinkageRequestAlgorithm {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "algorithm_id", nullable = false)
  private Algorithm algorithm;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "linkage_request_id", nullable = false)
  private LinkageRequest linkageRequest;

  public LinkageRequestAlgorithm( LinkageRequest linkageRequest ,Algorithm algorithm ) {
    this.linkageRequest = linkageRequest;
    this.algorithm = algorithm;
  }
}
