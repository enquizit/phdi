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
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/config/")
@RequiredArgsConstructor
public class AlgorithmConfigController {

  private final AlgorithmService algorithmService;
  private final BlockingFieldService blockingFieldService;
  private final CriteriaService criteriaService;

  @PostMapping("definition/create")
  public Mono<ResponseEntity<Void>> createAlgorithm(@RequestBody CreateAlgorithmRequest request) {
    return algorithmService.createAlgorithm(request);
  }

  @PostMapping("blocking-field/create")
  public Mono<ResponseEntity<Void>> createBlockingField(@RequestBody CreateBlockingFieldRequest request) {
    return blockingFieldService.createBlockingFields(request);

  }

  @PostMapping("criteria/create")
  public Mono<ResponseEntity<Void>> createCriteria(@RequestBody CreateCriteriaRequest request) {
    return criteriaService.createCriteria(request);
  }

  @GetMapping("view/{id}")
  public ResponseEntity<Mono<AlgorithmData>> findAlgorithmById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(algorithmService.getAlgorithmDataById(id));
  }

  @PutMapping("definition/edit")
  public Mono<ResponseEntity<Void>> updateAlgorithm(@RequestBody UpdateAlgorithmRequest request) {
    return algorithmService.updateAlgorithm(request);
  }

  @PutMapping("blocking-field/edit")
  public Mono<ResponseEntity<Void>> updateBlockingField(@RequestBody CreateBlockingFieldRequest request) {
    return blockingFieldService.updateBlockingFields(request);
  }

  @PutMapping("criteria/edit")
  public Mono<ResponseEntity<Void>> editCriteria(@RequestBody CreateCriteriaRequest request) {
    return  criteriaService.updateCriteria(request);
  }



}
