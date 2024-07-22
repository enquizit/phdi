package com.cdc.linkage.service;


import com.cdc.linkage.model.CreateCriteriaRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;



public interface CriteriaService {

  Mono<ResponseEntity<Void>> createCriteria(CreateCriteriaRequest request);

  Mono<ResponseEntity<Void>> updateCriteria(CreateCriteriaRequest request);

}
