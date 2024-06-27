package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.entities.BlockingField;
import com.cdc.linkage.entities.Field;
import com.cdc.linkage.exceptions.AlgorithmNotFoundException;
import com.cdc.linkage.model.Block;
import com.cdc.linkage.model.CreateBlockingFieldRequest;
import com.cdc.linkage.repository.AlgorithmRepository;
import com.cdc.linkage.repository.BlockingFieldRepository;
import com.cdc.linkage.repository.FieldRepository;
import com.cdc.linkage.repository.TransformationTypeRepository;
import com.cdc.linkage.service.AlgorithmService;
import com.cdc.linkage.service.BlockingFieldService;
import com.cdc.linkage.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BlockingFieldServiceImpl implements BlockingFieldService {


  private final BlockingFieldRepository blockingFieldRepository;
  private final AlgorithmRepository algorithmRepository;
  private final FieldService fieldService;
  private final AlgorithmService algorithmService;
  private final FieldRepository fieldRepository;



  public Mono<Void> createBlockingFields(CreateBlockingFieldRequest request) {
    return algorithmRepository.findById(request.algorithmId())
        .switchIfEmpty(Mono.error(new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId())))
        .flatMap(algorithm -> {
          return Flux.fromIterable(request.blocks())
              .flatMap(block ->
                  fieldService.findOrCreateField(block.fieldName())
                      .map(field -> new BlockingField(algorithm.getId(), field.getId(), block.transformationId()))
              )
              .collectList();
        })
        .flatMapMany(blockingFieldsToSave ->
            blockingFieldRepository.saveAll(blockingFieldsToSave)
        )
        .then();
  }



  @Override
  public Mono<Void> updateBlockingFields(CreateBlockingFieldRequest request) {
    return algorithmRepository.findById(request.algorithmId())  // Find algorithm by ID reactively
        .flatMap(algorithm -> updateBlockingFields(algorithm.getId(), request.blocks())).then();
  }



  private Mono<Void> updateBlockingFields(Long algorithmId, List<Block> blocks) {
    List<Mono<BlockingField>> saveMappings = blocks.stream()
        .map(requestBlock ->
            algorithmHaveTheBlockingField(algorithmId, requestBlock)
                .flatMap(fieldId -> {
                  if (fieldId != 0l) {
                    return blockingFieldRepository.findByAlgorithmIdAndFieldId(algorithmId, fieldId)
                        .flatMap(currentBlockField -> {
                          currentBlockField.setTransformationType(requestBlock.transformationId());
                          return blockingFieldRepository.save(currentBlockField);
                        });
                  } else {
                    return fieldService.findOrCreateField(requestBlock.fieldName())
                        .flatMap(field -> {
                          BlockingField newBlockingField = new BlockingField(
                              algorithmId, field.getId(), requestBlock.transformationId()
                          );
                          return blockingFieldRepository.save(newBlockingField);
                        });
                  }
                })
        )
        .toList();
    return Flux.merge(saveMappings).then();
  }


  private Mono<Long> algorithmHaveTheBlockingField(Long algorithmId, Block blockRequest) {
    return algorithmService.getAlgorithmDataById(algorithmId)
        .flatMap(algorithmData -> {
          // Check if the field exists in the Blocks
          boolean blockExists = algorithmData.getBlocks().containsKey(blockRequest.fieldName());
          if (blockExists) {
            return fieldRepository.findByName(blockRequest.fieldName())
                .map(Field::getId)  //  Return field ID if found
                .defaultIfEmpty(0L);  // Return 0 if the field is not found
          } else {
            return Mono.just(0L); // Return 0 if the v is not found
          }
        });
  }



}
