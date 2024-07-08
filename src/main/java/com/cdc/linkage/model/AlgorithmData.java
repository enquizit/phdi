package com.cdc.linkage.model;

import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.utils.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AlgorithmData {
  private Long id;
  private String name;
  private String type;
  private Map<String, String> funcs;
  private Map<String, String> blocks;
  private Map<String, String> params;
  private Map<String, Double> thresholds;

  public AlgorithmData(List<AlgorithmResultDto> rows) {
    AlgorithmResultDto firstRow = rows.get(0);
    this.id = firstRow.getAlgorithmId();
    this.name = firstRow.getAlgorithmName();
    this.type = firstRow.getAlgorithmType();

    // Populate the maps for params, blocks, funcs and thresholds
    this.funcs = new HashMap<>();
    this.blocks = new HashMap<>();
    this.params = new HashMap<>();
    this.thresholds = new HashMap<>();

    for (AlgorithmResultDto row : rows) {
      if (row.getParamName() != null) {
        this.getParams().put(row.getParamName(), row.getParamValue());
      }
      if (row.getBlockFieldName() != null) {
        String transformation =
            row.getTransformationType() != null ? row.getTransformationType() : Constant.NO_TRANSFORMATION;
        this.getBlocks().put(row.getBlockFieldName(), transformation);
      }
      if (row.getCriteriaFieldName() != null) {
        this.getFuncs().put(row.getCriteriaFieldName(), row.getFunctionName());
        this.getThresholds().put(row.getCriteriaFieldName(), row.getCriteria_field_threshold());
      }
    }
  }


  @JsonIgnore
  public Map<String, Double> getLogOdds() {
    ObjectMapper mapper = new ObjectMapper();
    if (params.containsKey("log_odds")) {
      String logOddsJsonStr = params.get("log_odds");
      try {
        return mapper.readValue(logOddsJsonStr, Map.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return Collections.emptyMap();
  }

}
