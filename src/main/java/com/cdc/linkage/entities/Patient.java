package com.cdc.linkage.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
public class Patient {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  private String key;
  private String value;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "person_id")
  private Person person;

  public Patient(Map.Entry<String,String> entry,Person person, LocalDateTime createdAt) {
    this.key = entry.getKey();
    this.value = entry.getValue();
    this.person = person;
    this.createdAt = createdAt;
  }
  public Patient(Map.Entry<String,String> entry, LocalDateTime createdAt) {
    this.key = entry.getKey();
    this.value = entry.getValue();
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Patient patient = (Patient) o;
    return Objects.equals(person, patient.person);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(person);
  }
}
