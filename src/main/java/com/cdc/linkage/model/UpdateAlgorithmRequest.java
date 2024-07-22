package com.cdc.linkage.model;

import java.util.List;

public record UpdateAlgorithmRequest(
    Long algorithmId,
    String algorithmName,
    List<ParameterRequest> parameters
) {
}
