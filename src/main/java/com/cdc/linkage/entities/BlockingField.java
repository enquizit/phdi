package com.cdc.linkage.entities;



import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * BlockingField entity.
 * Details: Holds the blocking fields that will be used in the algorithm to fetch the
 * matching records from the database with their transformation types.
 */


@Table(name = "blocking_field")
@Data
@NoArgsConstructor
public class BlockingField {

  @Id
  private Long id;
  private Long algorithmId;
  private Long fieldId;
  private Long transformationType;


  public BlockingField(Long algorithmId,Long fieldId, Long transformationType ) {
    this.algorithmId = algorithmId;
    this.fieldId = fieldId;
    this.transformationType = transformationType;
  }
}
