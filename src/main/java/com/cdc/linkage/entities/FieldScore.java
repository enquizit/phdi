package com.cdc.linkage.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "field_score")
@Getter
@NoArgsConstructor
public class FieldScore {

  @Id
  private Long id;
  private UUID requestId;
  private UUID personId;
  private UUID patientId;
  private String fieldName;
  private Double score;


  public FieldScore(UUID requestId,UUID personId,UUID patientId, String fieldName, Double score) {
    this.requestId = requestId;
    this.personId = personId;
    this.patientId = patientId;
    this.fieldName = fieldName;
    this.score = score;
  }
}
