package com.cdc.linkage.service;



import com.cdc.linkage.entities.Algorithm;

import java.util.List;
import java.util.UUID;

public interface LinkageRequestAlgorithmService {

  UUID saveLinkageRequest(List<Algorithm> algorithmsList);

}
