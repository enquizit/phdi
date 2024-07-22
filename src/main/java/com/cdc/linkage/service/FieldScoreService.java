package com.cdc.linkage.service;

import com.cdc.linkage.entities.FieldScore;
import reactor.core.publisher.Mono;

public interface FieldScoreService {

  Mono<FieldScore> saveFieldScore(FieldScore fieldScore);
}
