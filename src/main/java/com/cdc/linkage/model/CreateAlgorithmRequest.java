package com.cdc.linkage.model;

import java.util.List;

public record CreateAlgorithmRequest(
    String name,
    String type,
    List<ParameterRequest> parameters
) {
}
