package com.cdc.linkage.service;

import com.cdc.linkage.model.InsertionResponse;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface LinkageService {

  InsertionResponse recordLinkage(RecordLinkageRequest request) throws JsonProcessingException;

}
