package com.cdc.linkage.model;

import java.util.List;
import java.util.Map;

public record RecordLinkageRequest(List<Long> algorithmId, Map<String,String> patientRecord

) {
}



