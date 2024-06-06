package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.entities.LinkageRequest;
import com.cdc.linkage.entities.LinkageRequestAlgorithm;
import com.cdc.linkage.repository.LinkageRequestAlgorithmRepository;
import com.cdc.linkage.repository.LinkageRequestRepository;
import com.cdc.linkage.service.LinkageRequestAlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkageRequestAlgorithmServiceImpl implements LinkageRequestAlgorithmService {

  private final LinkageRequestAlgorithmRepository linkageRequestAlgorithmRepository;

  @Override
  public UUID saveLinkageRequest(List<Algorithm> algorithmsList) {
    LinkageRequest linkageRequest =new LinkageRequest();
    List<LinkageRequestAlgorithm> linkageRequestAlgorithms = new ArrayList<>();
     for (Algorithm algorithm :algorithmsList ){
       linkageRequestAlgorithms.add(new LinkageRequestAlgorithm(linkageRequest,algorithm));
     }
    LinkageRequestAlgorithm linkageRequestAlgorithm= linkageRequestAlgorithmRepository.saveAll(linkageRequestAlgorithms).get(0);
  return linkageRequestAlgorithm.getId();
  }
}
