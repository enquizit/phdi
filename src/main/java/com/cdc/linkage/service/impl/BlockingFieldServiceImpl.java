package com.cdc.linkage.service.impl;


import com.cdc.linkage.entities.*;
import com.cdc.linkage.exceptions.AlgorithmNotFoundException;
import com.cdc.linkage.exceptions.TransformationNotFoundException;
import com.cdc.linkage.model.Block;
import com.cdc.linkage.model.CreateBlockingFieldRequest;
import com.cdc.linkage.model.ParameterRequest;
import com.cdc.linkage.repository.*;
import com.cdc.linkage.service.BlockingFieldService;
import com.cdc.linkage.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BlockingFieldServiceImpl implements BlockingFieldService {


  private final BlockingFieldRepository blockingFieldRepository;
  private final AlgorithmRepository algorithmRepository;
  private final TransformationTypeRepository transformationTypeRepository;
  private final FieldService fieldService;


  @Transactional
  public void createBlockingFields(CreateBlockingFieldRequest request) {
    Algorithm algorithm = algorithmRepository.findById(request.algorithmId())
        .orElseThrow(() -> new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId()));
    List<BlockingField> blockingFieldsToSave = new ArrayList<>();
    for (Block block : request.blocks()) {
      TransformationType transformationType = getTransformationType(block.transformationId());
      // Check if the field already exists, otherwise create a new one
      Field field = fieldService.findOrCreateField(block.fieldName());
      blockingFieldsToSave.add(new BlockingField(algorithm, field, transformationType));
    }
    blockingFieldRepository.saveAll(blockingFieldsToSave);
  }

  @Transactional
  public void updateBlockingFields(CreateBlockingFieldRequest request) {
    Algorithm algorithm = algorithmRepository.findById(request.algorithmId())
        .orElseThrow(() -> new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId()));
    List<BlockingField> blockingFieldsToSave = new ArrayList<>();
    for (Block block : request.blocks()) {
      TransformationType transformationType = getTransformationType(block.transformationId());
      BlockingField blockingField = algorithmHaveTheBlockingField(block, algorithm);
      if (blockingField != null) {
        blockingField.setTransformationType(transformationType);
        blockingFieldRepository.save(blockingField);
      } else {
        Field field = fieldService.findOrCreateField(block.fieldName());
        blockingFieldsToSave.add(new BlockingField(algorithm, field, transformationType));
      }
    }
    if (!blockingFieldsToSave.isEmpty()) {
      blockingFieldRepository.saveAll(blockingFieldsToSave);
    }
  }

  private BlockingField algorithmHaveTheBlockingField(Block block, Algorithm algorithm) {
    for (BlockingField blockingField : algorithm.getBlockingFields()) {
      if (blockingField.getField().getName().equals(block.fieldName()))
        return blockingField;
    }
    return null;
  }


  private TransformationType getTransformationType(Long transformationId) {
    return transformationTypeRepository.findById(transformationId)
        .orElseThrow(
            () -> new TransformationNotFoundException("No transformation with id =" + transformationId));
  }
}
