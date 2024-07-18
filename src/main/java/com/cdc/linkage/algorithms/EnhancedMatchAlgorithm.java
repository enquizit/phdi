package com.cdc.linkage.algorithms;

import com.cdc.linkage.entities.FieldScore;
import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.cdc.linkage.service.FieldScoreService;
import com.cdc.linkage.utils.LoggerUtil;
import com.cdc.linkage.utils.StringSimilarity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cdc.linkage.utils.Constant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnhancedMatchAlgorithm implements MatchAlgorithm {

  private final LoggerUtil loggerUtil;
  private final FieldScoreService fieldScoreService;

  private RecordLinkageRequest request;
  private UUID requestId;


  @Override
  public boolean matchRecord(UUID patientId, Map<String, String> recordFields, AlgorithmData algorithmData,
      RecordLinkageRequest request, UUID reqId, UUID personId) {
    this.requestId = reqId;
    this.request = request;
    List<Double> recordScores = new ArrayList<>();
    for (Map.Entry<String, String> field : recordFields.entrySet()) {
      if (request.patientRecord().containsKey(field.getKey()) && algorithmData.getFuncs().containsKey(field.getKey())) {
        recordScores.add(compare(field.getKey(), field.getValue(), algorithmData,personId,patientId));
      }
    }
    String matchingRule = algorithmData.getParams().get("matching_rule");
    if (matchingRule.equals("eval_log_odds_cutoff"))
      return evalLogOddsCutoff(recordScores, algorithmData);
    else
      throw new IllegalArgumentException("No Suitable compare Method");

  }

  private Double compare(String field, String fieldDatabaseValue, AlgorithmData algorithmData,UUID personId,UUID patientId) {
    if (algorithmData.getFuncs().get(field).equals("feature_match_log_odds_fuzzy_compare"))
      return compareLogOddsFuzzy(field, fieldDatabaseValue, algorithmData,personId,patientId);
    if (algorithmData.getFuncs().get(field).equals("feature_match_log_odds_exact"))
      return compareLogOddsExact(field, fieldDatabaseValue, algorithmData,personId, patientId);
    else
      throw new IllegalArgumentException("No Suitable compare Method");
  }

  private Double compareLogOddsFuzzy(String field, String fieldDatabaseValue, AlgorithmData algorithmData,UUID personId,UUID patientId) {
    String fieldRequestValue = request.patientRecord().get(field);
    Double score;
    String similarityMeasure = algorithmData.getParams().getOrDefault("similarity_measure", "JaroWinkler");
    Double threshold = algorithmData.getThresholds().getOrDefault(field, 0.7);
    Double fieldLogOdds = algorithmData.getLogOdds().get(field);
    score = StringSimilarity.compareStrings(fieldRequestValue, fieldDatabaseValue, similarityMeasure);
    score = (score < threshold) ? 0 : score;
    loggerUtil.compareLogOddsThreshold(requestId, field, threshold, fieldRequestValue, fieldDatabaseValue, score,
        fieldLogOdds, personId, patientId);
    Double finalScore = score * fieldLogOdds;
    fieldScoreService.saveFieldScore(new FieldScore(requestId, personId, patientId, field, finalScore)).subscribe();
    return finalScore;
  }

  private Double compareLogOddsExact(String field, String fieldDatabaseValue,AlgorithmData algorithmData, UUID personId, UUID patientId) {
    String fieldRequestValue = request.patientRecord().get(field);
    Double fieldLogOdds = algorithmData.getLogOdds().get(field);
    Double score = (fieldRequestValue.equalsIgnoreCase(fieldDatabaseValue)) ? fieldLogOdds : 0;
    loggerUtil.compareLogOdds(requestId, field, fieldRequestValue, fieldDatabaseValue, score, personId, patientId);
    fieldScoreService.saveFieldScore(new FieldScore(requestId, personId, patientId, field, score)).subscribe();
    return score;
  }

  private boolean evalLogOddsCutoff(List<Double> recordScores, AlgorithmData algorithmData) {
    double trueMatchThreshold = Double.parseDouble(algorithmData.getParams().get("true_match_threshold"));
    double sumOfScores = recordScores.stream().mapToDouble(Double::doubleValue).sum();

    if (sumOfScores >= trueMatchThreshold) {
      log(SCORE_GREATER_THAN_TRUE_MATCH, sumOfScores, trueMatchThreshold);
      return true;
    }

    if (algorithmData.getParams().containsKey("lower_match_threshold")) {
      double lowerMatchThreshold = Double.parseDouble(algorithmData.getParams().get("lower_match_threshold"));
      if (sumOfScores >= lowerMatchThreshold) {
        log(SCORE_GREATER_THAN_LOWER_MATCH, sumOfScores, lowerMatchThreshold);
        return true;
      } else {
        log(SCORE_LESS_THAN_LOWER_MATCH, sumOfScores, lowerMatchThreshold);
        return false;
      }
    }

    log(SCORE_LESS_THAN_TRUE_MATCH, sumOfScores, trueMatchThreshold);
    return false;
  }


  private void log(String message, double sumOfScores, double matchThreshold) {
    String formatedMessage = String.format(message, requestId.toString(), sumOfScores, matchThreshold);
    loggerUtil.evalLogOddsCutoff(requestId, formatedMessage);
  }


}
