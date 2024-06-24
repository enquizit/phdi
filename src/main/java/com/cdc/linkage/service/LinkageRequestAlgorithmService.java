package com.cdc.linkage.service;




import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.entities.LinkageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LinkageRequestAlgorithmService {

   Mono<LinkageRequest> saveLinkageRequest(Flux<Algorithm> algorithmsFlux);

}
