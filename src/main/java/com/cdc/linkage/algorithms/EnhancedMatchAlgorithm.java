package com.cdc.linkage.algorithms;

import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.cdc.linkage.utils.StringSimilarity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class EnhancedMatchAlgorithm implements MatchAlgorithm {

  private AlgorithmData algorithmData;
  private RecordLinkageRequest request;

  @Override
  public boolean matchRecord(Map<String, String> recordFields, AlgorithmData algorithmData,
      RecordLinkageRequest request) {
    this.algorithmData = algorithmData;
    this.request = request;

    List<Double> recordScores = new ArrayList<>();
    for (Map.Entry<String, String> field : recordFields.entrySet()) {
      if (request.patientRecord().containsKey(field.getKey())) {
        if (algorithmData.getFuncs().containsKey(field.getKey())) {
          recordScores.add(compare(field.getKey(), field.getValue()));
        }
      }
    }
    String matchingRule = algorithmData.getParams().get("matching_rule");
    if (matchingRule.equals("eval_log_odds_cutoff"))
      return evalLogOddsCutoff(recordScores);
    else
      throw new IllegalArgumentException("No Suitable compare Method");
  }

  private Double compare(String field, String fieldDatabaseValue) {
    if (algorithmData.getFuncs().get(field).equals("feature_match_log_odds_fuzzy_compare"))
      return compareLogOddsFuzzy(field, fieldDatabaseValue);
    else
      throw new IllegalArgumentException("No Suitable compare Method");
  }

  private Double compareLogOddsFuzzy(String field, String fieldDatabaseValue) {
    String fieldRequestValue = request.patientRecord().get(field);
    Double score;
    String similarityMeasure = algorithmData.getParams().getOrDefault("similarity_measure", "JaroWinkler");
    Double threshold = Double.parseDouble(algorithmData.getParams().getOrDefault("threshold", ".7"));
    Double fieldLogOdds = algorithmData.getLogOdds().get(field);
    score = StringSimilarity.compareStrings(fieldRequestValue, fieldDatabaseValue, similarityMeasure);
    score = (score < threshold) ? 0 : score;
    //logggging
    log.info("Field : {} - Values (request record, retrieved record) : {} " +
            "- Comparison score : {} " +
            " - Score, threshold comparison (score < threshold) :{} " +
            "- Final score (score * field’s log-odds) : {}",
        field,
        ("(" + fieldRequestValue + "," + fieldDatabaseValue + ")"),
        score,
        (score < threshold),
        (score + " * " + fieldLogOdds + " = " + score * fieldLogOdds)

    );
    //logggging
    return score * fieldLogOdds;
  }

  private boolean evalLogOddsCutoff(List<Double> recordScores) {
    double trueMatchThreshold = Double.parseDouble(algorithmData.getParams().get("true_match_threshold"));
    double sumOfScores = recordScores.stream().mapToDouble(Double::doubleValue).sum();
    //logging
    if (sumOfScores < trueMatchThreshold) {
      log.info("Result:  Not a true match since the sum of scores: :{} " +
              "is less than the provided “true_match_threshold”: : {}",
          sumOfScores,
          trueMatchThreshold
      );
    } else {
      log.info("Result:  A true match since the sum of scores: :{} " +
              "is greater than the provided “true_match_threshold”: :{}",
          sumOfScores,
          trueMatchThreshold
      );
    }
    //logging
    return sumOfScores >= trueMatchThreshold;
  }


}
