package com.cdc.linkage.service.impl;


import com.cdc.linkage.entities.AlgorithmVersion;
import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.repository.AlgorithmVersionRepository;
import com.cdc.linkage.service.AlgorithmService;
import com.cdc.linkage.service.AlgorithmVersionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AlgorithmVersionServiceImpl implements AlgorithmVersionService {

  private final AlgorithmVersionRepository algorithmVersionRepository;


  @Override
  public Mono<Void> saveAlgorithmVersion(AlgorithmData algorithmData) {
    if (isAlgorithmCompleted(algorithmData)) {
      return getLastVersionNumber(algorithmData.getId())
          .flatMap(versionId -> {
            AlgorithmVersion algorithmVersion = AlgorithmVersion.builder()
                .algorithmId(algorithmData.getId())
                .versionId(++versionId)
                .algorithmJson(getAlgorithmJson(algorithmData))
                .createdAt(LocalDateTime.now()).build();
            return algorithmVersionRepository.save(algorithmVersion).then();
          });
    }
    return Mono.empty();
  }


  boolean isAlgorithmCompleted(AlgorithmData algorithmData) {
    return !(algorithmData.getParams().isEmpty() || algorithmData.getBlocks().isEmpty() || algorithmData.getFuncs()
        .isEmpty());
  }


  private String getAlgorithmJson(AlgorithmData algorithmData) {
    try {
      return new ObjectMapper().writeValueAsString(algorithmData);
    } catch (JsonProcessingException e) {
      return "error:Failed to convert to JSON";
    }
  }

  public Mono<Long> getLastVersionNumber(Long algorithmId) {
    return algorithmVersionRepository.findMaxVersionForAlgorithm(algorithmId)
        .defaultIfEmpty(0L);
  }

}



