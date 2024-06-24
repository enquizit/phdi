package com.cdc.linkage.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogData(UUID requestId , String message, LocalDateTime timestamp ) {
}
