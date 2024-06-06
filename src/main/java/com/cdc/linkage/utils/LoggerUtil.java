package com.cdc.linkage.utils;


import com.cdc.linkage.entities.Patient;
import com.cdc.linkage.entities.Person;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cdc.linkage.utils.Constant.*;

@Slf4j
public class LoggerUtil {


  public static void requestData(UUID requestId, String algorithmType, String requestJson) {
    log.info(REQUEST_MESSAGE, requestId, algorithmType, requestJson);
  }

  public static void recordsPerAlgorithm(UUID requestId,int recordsCount) {
    log.info(RECORDS_PER_ALGORITHM, requestId,recordsCount);
  }

  public static void personBelongRatio(UUID personId, double ratio) {
    //log.info(PERSON_BELONG_RATIO, personId, ratio);
  }

  public static void personFinalScore(UUID requestId, Map<Person, Double> linkageScores) {
//    log.info(START_FINDING_PERSON_SCORE);
//    for (Map.Entry<Person, Double> personScore : linkageScores.entrySet()) {
//      log.info(PERSON_FINAL_SCORE, requestId, personScore.getKey().getId(), personScore.getValue());
//    }
  }

  public static void fieldRecordsCount(UUID reqId ,String fieldName, String transformation, String fieldValue, boolean isFound) {
    String description = (isFound ? "Match found for" : "No Match found for ") + "'" + fieldValue + "'";
    log.info(FIELD_RECORDS_COUNT, reqId,fieldName, transformation, description);
  }

  public static void fieldNotIncludedInRequest(String fieldName, String transformation) {
    //log.info(FIELD_NOT_IN_REQUEST, fieldName, transformation);
  }

  public static void matchedRecords(UUID reqId,UUID personId, Map<String, String> patientRecord) {
    log.info(RECORD_DATA,reqId, personId,  patientRecord);
  }


}
