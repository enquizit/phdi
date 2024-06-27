package com.cdc.linkage.model;

import java.util.List;

public record CreateCriteriaRequest(
    Long algorithmId,
   List<CriteriaFunction> CriteriaFunctions
) {}
