package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.FieldScore;
import com.cdc.linkage.repository.FieldScoreRepository;
import com.cdc.linkage.service.FieldScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FieldScoreServiceImpl implements FieldScoreService {

  private final FieldScoreRepository fieldScoreRepository;

  @Override
  public Mono<FieldScore> saveFieldScore(FieldScore fieldScore) {
    return fieldScoreRepository.save(fieldScore);
  }
}
