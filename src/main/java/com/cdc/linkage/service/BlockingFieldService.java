package com.cdc.linkage.service;


import com.cdc.linkage.model.CreateBlockingFieldRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;



public interface BlockingFieldService {

   Mono<ResponseEntity<Void>>  createBlockingFields(CreateBlockingFieldRequest request);

   Mono<ResponseEntity<Void>>  updateBlockingFields(CreateBlockingFieldRequest request);
}
