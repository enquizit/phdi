package com.cdc.linkage.model;

import java.util.List;

public record CreateBlockingFieldRequest(
    Long algorithmId,
    List<Block> blocks
) {}
