package com.cdc.linkage.model;

import java.time.LocalDate;

public record DateRangeRequest(LocalDate startDate, LocalDate endDate) {
}
