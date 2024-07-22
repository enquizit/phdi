package com.cdc.linkage.service;


import com.cdc.linkage.model.AlgorithmData;
import reactor.core.publisher.Mono;

public interface AlgorithmVersionService {

    Mono<Void> saveAlgorithmVersion(AlgorithmData algorithmData);

    Mono<Long> getLastVersionNumber(Long algorithmId);


}
