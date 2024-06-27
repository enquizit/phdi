package com.cdc.linkage.service;

import com.cdc.linkage.model.InsertionResponse;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

public interface LinkageService {

  Mono<InsertionResponse> recordLinkage(RecordLinkageRequest request) throws JsonProcessingException;

}
