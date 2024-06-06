package com.cdc.linkage.service;

import com.cdc.linkage.entities.AlgorithmParameter;
import com.cdc.linkage.model.ParameterRequest;

public interface AlgorithmParameterService {
   AlgorithmParameter findOrCreateParameter(ParameterRequest paramRequest);
}
