package com.cdc.linkage.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "external_person")
public class ExternalPerson {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  private Person person;
}
