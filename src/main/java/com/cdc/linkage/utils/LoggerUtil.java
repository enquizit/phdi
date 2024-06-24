package com.cdc.linkage.utils;


import com.cdc.linkage.entities.LinkageRequest;
import com.cdc.linkage.entities.LogItem;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.cdc.linkage.service.LoggingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static com.cdc.linkage.utils.Constant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggerUtil {

  private final LoggingService loggingService;
  static ObjectMapper mapper = new ObjectMapper();

  public  void requestData(UUID linkageRequest, String algorithmType, RecordLinkageRequest request) {
    String requestJson = null;
    try {
      requestJson = mapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    String message = String.format(REQUEST_MESSAGE,linkageRequest.toString(), algorithmType, requestJson);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequest,message)).subscribe();
  }


  public  void fieldRecordsCount(UUID linkageRequest ,String fieldName, String transformation, String fieldValue, boolean isFound) {
    String description = (isFound ? "Match found for" : "No Match found for ") + "'" + fieldValue + "'";
    String message = String.format(FIELD_RECORDS_COUNT,linkageRequest.toString(), fieldName ,transformation, description);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequest,message)).subscribe();
  }

  public void recordsPerAlgorithm(UUID linkageRequest,Long algorithmId ,int recordsCount) {
    String message = String.format(RECORDS_PER_ALGORITHM,linkageRequest.toString(),algorithmId.toString(), recordsCount);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequest,message)).subscribe();
  }

  public  void matchedRecords(UUID linkageRequest,UUID personId, Map<String, String> patientRecord) {
    String message = String.format(RECORD_DATA,linkageRequest.toString(), personId.toString() ,patientRecord);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequest,message)).subscribe();
  }

}
