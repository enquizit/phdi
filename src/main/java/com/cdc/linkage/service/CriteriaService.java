package com.cdc.linkage.service;


import com.cdc.linkage.model.CreateCriteriaRequest;
import reactor.core.publisher.Mono;



public interface CriteriaService {

  Mono<Void> createCriteria(CreateCriteriaRequest request);

  Mono<Void> updateCriteria(CreateCriteriaRequest request);

}
