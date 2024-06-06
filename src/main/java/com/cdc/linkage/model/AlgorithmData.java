package com.cdc.linkage.model;

import com.cdc.linkage.entities.Algorithm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class AlgorithmData {
  private Long id;
  private String name;
  private String type;
  private Map<String, String> funcs;
  private Map<String, String> blocks;
  private Map<String, String> params;

  public AlgorithmData(Algorithm algorithm) {
    id = algorithm.getId();
    name = algorithm.getName();
    type = algorithm.getType();
    this.funcs = algorithm.getCriteriaList().stream()
        .collect(Collectors.toMap(
            c -> c.getField().getName(),
            c -> c.getFunctionName()
        ));

    this.params = algorithm.getParameterMappings().stream()
        .collect(Collectors.toMap(
            p -> p.getAlgorithmParameter().getName(),
            p -> p.getParameterValue()
        ));

    this.blocks = algorithm.getBlockingFields().stream()
        .collect(Collectors.toMap(
            b -> b.getField().getName().toLowerCase(),
            b -> b.getTransformationType().getName()
        ));

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
