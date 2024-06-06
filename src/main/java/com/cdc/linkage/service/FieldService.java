package com.cdc.linkage.service;

import com.cdc.linkage.entities.Field;

public interface FieldService {

  public Field findOrCreateField(String fieldName);
}
