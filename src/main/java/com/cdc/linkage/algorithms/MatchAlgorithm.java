package com.cdc.linkage.algorithms;

import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.RecordLinkageRequest;

import java.util.Map;
import java.util.UUID;

public interface MatchAlgorithm {
  boolean matchRecord(Map<String, String> recordFields, AlgorithmData algorithmData, RecordLinkageRequest request, UUID reqId);
}
