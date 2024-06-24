package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.Field;
import com.cdc.linkage.repository.FieldRepository;
import com.cdc.linkage.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

  private final FieldRepository fieldRepository;

  public Mono<Field> findOrCreateField(String fieldName) {
    return fieldRepository.findByName(fieldName)
        .switchIfEmpty(
            fieldRepository.save(new Field(fieldName))
        );
  }


}
