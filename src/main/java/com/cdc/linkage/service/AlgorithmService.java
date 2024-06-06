package com.cdc.linkage.service;



import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.model.CreateAlgorithmRequest;
import com.cdc.linkage.model.UpdateAlgorithmRequest;

import java.util.List;

public interface AlgorithmService {

   Algorithm createAlgorithm(CreateAlgorithmRequest request);

   Algorithm findAlgorithmById(long id);

   List<Algorithm> findAlgorithmsByIds(List<Long> ids);

   Algorithm UpdateAlgorithm(UpdateAlgorithmRequest request);

}
