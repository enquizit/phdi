package com.cdc.linkage.controller;

import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.model.*;
import com.cdc.linkage.service.AlgorithmService;
import com.cdc.linkage.service.BlockingFieldService;
import com.cdc.linkage.service.CriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/config/")
@RequiredArgsConstructor
public class AlgorithmConfigController {

  private final AlgorithmService algorithmService;
  private final BlockingFieldService blockingFieldService;
  private final CriteriaService criteriaService;

  @PostMapping("definition/create")
  public ResponseEntity<Long> createAlgorithm(@RequestBody CreateAlgorithmRequest request) {
    Algorithm createdAlgorithm = algorithmService.createAlgorithm(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdAlgorithm.getId());
  }

  @PutMapping("definition/edit")
  public ResponseEntity<Long> updateAlgorithm(@RequestBody UpdateAlgorithmRequest request) {
    Algorithm createdAlgorithm = algorithmService.UpdateAlgorithm(request);
    return ResponseEntity.status(HttpStatus.OK).body(createdAlgorithm.getId());
  }

  @PostMapping("blocking-field/create")
  public ResponseEntity<Void> createBlockingField(@RequestBody CreateBlockingFieldRequest request) {
    blockingFieldService.createBlockingFields(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("blocking-field/edit")
  public ResponseEntity<Void> updateBlockingField(@RequestBody CreateBlockingFieldRequest request) {
    blockingFieldService.updateBlockingFields(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }


  @PostMapping("criteria/create")
  public ResponseEntity<Void> createCriteria(@RequestBody CreateCriteriaRequest request) {
    criteriaService.createCriteria(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("criteria/edit")
  public ResponseEntity<Void> editCriteria(@RequestBody CreateCriteriaRequest request) {
    criteriaService.updateCriteria(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }


  @GetMapping("view/{id}")
  public ResponseEntity<AlgorithmData> findAlgorithmById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(new AlgorithmData(algorithmService.findAlgorithmById(id)));
  }



}


