package com.cdc.linkage.service;


import com.cdc.linkage.model.CreateBlockingFieldRequest;
import reactor.core.publisher.Mono;



public interface BlockingFieldService {

   Mono<Void> createBlockingFields(CreateBlockingFieldRequest request);

   Mono<Void> updateBlockingFields(CreateBlockingFieldRequest request);
}
