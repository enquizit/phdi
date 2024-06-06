package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.Field;
import com.cdc.linkage.repository.FieldRepository;
import com.cdc.linkage.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

  private final FieldRepository fieldRepository;

  public Field findOrCreateField(String fieldName) {
    return fieldRepository.findByName(fieldName)
        .orElseGet(() -> {
          Field newField = new Field();
          newField.setName(fieldName);
          fieldRepository.save(newField);
          return newField;
        });
  }
}
