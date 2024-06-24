package com.cdc.linkage.service;

import com.cdc.linkage.entities.Field;
import reactor.core.publisher.Mono;

public interface FieldService {

  public Mono<Field> findOrCreateField(String fieldName);
}
