package com.cdc.linkage.utils;


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

  public void requestData(UUID linkageRequestId, String algorithmType, RecordLinkageRequest request) {
    String requestJson = null;
    try {
      requestJson = mapper.writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    String message = String.format(REQUEST_MESSAGE, linkageRequestId.toString(), algorithmType, requestJson);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void fieldRecordsCount(UUID linkageRequestId, String fieldName, String transformation, String fieldValue,
      boolean isFound) {
    String description = (isFound ? "Match found for" : "No Match found for ") + "'" + fieldValue + "'";
    String message =
        String.format(FIELD_RECORDS_COUNT, linkageRequestId.toString(), fieldName, transformation, description);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void recordsPerAlgorithm(UUID linkageRequestId, Long algorithmId, int recordsCount) {
    String message =
        String.format(RECORDS_PER_ALGORITHM, linkageRequestId, algorithmId.toString(), recordsCount);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void matchedRecords(UUID linkageRequestId, UUID personId, Map<String, String> patientRecord) {
    String message = String.format(RECORD_DATA, linkageRequestId.toString(), personId.toString(), patientRecord);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void processAlgorithm(UUID linkageRequestId, Long algorithmId) {
    String message = String.format(PROCESS_ALGORITHM, linkageRequestId.toString(), algorithmId);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void clusterTotal(UUID linkageRequestId, UUID personId, int numMatchedInCluster) {
    String message = String.format(CLUSTER_TOTAL, linkageRequestId.toString(), personId, numMatchedInCluster);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void newPerson(UUID linkageRequestId) {
    String message = String.format(NEW_PERSON, linkageRequestId.toString());
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void noOtherCandidates(UUID linkageRequestId, Map<UUID, Double> linkageScores) {
    Map.Entry<UUID, Double> firstEntry = linkageScores.entrySet().iterator().next();
    String message = String.format(NO_OTHER_CANDIDATES, linkageRequestId.toString(), firstEntry.getKey());
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void strongestMatch(UUID linkageRequestId, UUID personId) {
    String message = String.format(STRONGEST_MATCH, linkageRequestId.toString(), personId);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void currentPerson(UUID linkageRequestId) {
    String message = String.format(CURRENT_PERSON, linkageRequestId.toString());
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void linkageScores(UUID linkageRequestId, Map<UUID, Double> linkageScores) {
    StringBuilder linkageScoresStr = new StringBuilder(" Linkage scores = {");
    for (Map.Entry<UUID, Double> entry : linkageScores.entrySet()) {
      linkageScoresStr.append("{").append(entry.getKey()).append(" : ").append(entry.getValue()).append("}");
    }
    linkageScoresStr.append("}");
    String message = String.format(LINKAGE_SCORES, linkageRequestId.toString(), linkageScoresStr);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void belongingRatioMessage(UUID linkageRequestId, UUID personId, double belongingRatio) {
    String message = String.format(BELONGING_RATIO_MESSAGE, linkageRequestId.toString(), personId, belongingRatio);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void clusterRatio(UUID linkageRequestId, double belongingRatio, double clusterRatio) {
    String message = String.format(CLUSTER_RATIO, linkageRequestId.toString(), belongingRatio, clusterRatio);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void compareLogOdds(UUID linkageRequestId, String field, Double threshold, String fieldRequestValue,
      String fieldDatabaseValue,
      Double score, Double fieldLogOdds) {

    String fieldValues = ("(" + fieldRequestValue + "," + fieldDatabaseValue + ")");
    boolean scoreComparison = (score < threshold);
    String finalScore = (score + " * " + fieldLogOdds + " = " + score * fieldLogOdds);

    String message = String.format(COMPARE_LOG_ODDS,
        linkageRequestId.toString(),
        field,
        threshold,
        fieldValues,
        score,
        scoreComparison,
        finalScore);
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }

  public void evalLogOddsCutoff(UUID linkageRequestId, double sumOfScores, double trueMatchThreshold) {
    String message;
    if (sumOfScores < trueMatchThreshold) {
      message = String.format(SCORE_LESS_THAN_TRUE_MATCH, linkageRequestId.toString(), sumOfScores, trueMatchThreshold);
    } else {
      message = String.format(SCORE_GREATER_THAN_TRUE_MATCH, linkageRequestId.toString(), sumOfScores, trueMatchThreshold);
    }
    log.info(message);
    loggingService.saveLog(new LogItem(linkageRequestId, message)).subscribe();
  }


}
