package com.cdc.linkage.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Table(name = "patient")
@Data
@NoArgsConstructor
public class Patient {

  @Id
  private UUID id;
  private String key;
  private String value;
  private LocalDateTime createdAt;
  private UUID personId;
  private UUID patientId;

  public Patient(Map.Entry<String, String> entry, LocalDateTime createdAt, UUID personId, UUID patientId) {
    this.key = entry.getKey();
    this.value = entry.getValue();
    this.createdAt = createdAt;
    this.personId = personId;
    this.patientId = patientId;
  }
}
