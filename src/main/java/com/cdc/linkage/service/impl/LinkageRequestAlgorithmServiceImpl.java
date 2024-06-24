package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.entities.LinkageRequest;
import com.cdc.linkage.entities.LinkageRequestAlgorithm;
import com.cdc.linkage.repository.LinkageRequestAlgorithmRepository;
import com.cdc.linkage.repository.LinkageRequestRepository;
import com.cdc.linkage.service.LinkageRequestAlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
@RequiredArgsConstructor
public class LinkageRequestAlgorithmServiceImpl implements LinkageRequestAlgorithmService {

  private final LinkageRequestAlgorithmRepository linkageRequestAlgorithmRepository;
  private final LinkageRequestRepository linkageRequestRepository;


  @Override
  public Mono<LinkageRequest> saveLinkageRequest(Flux<Algorithm> algorithmsFlux) {
    return linkageRequestRepository.save(new LinkageRequest())  // Save the initial LinkageRequest with a generated UUID
        .flatMap(linkageRequest -> algorithmsFlux  // For each algorithm in the flux
            .flatMap(algorithm ->
                linkageRequestAlgorithmRepository.save(new LinkageRequestAlgorithm(algorithm.getId(), linkageRequest.getId())))
            .then(Mono.just(linkageRequest)));  // Return the ID of the saved linkageRequest
  }


}
