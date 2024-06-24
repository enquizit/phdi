package com.cdc.linkage.algorithms;

import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.RecordLinkageRequest;
//import com.cdc.linkage.utils.StringSimilarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicMatchAlgorithm implements MatchAlgorithm {

  private AlgorithmData algorithmData;
  private RecordLinkageRequest request;


  @Override
  public boolean matchRecord(Map<String, String> recordFields, AlgorithmData algorithmData,
      RecordLinkageRequest request) {
    this.algorithmData = algorithmData;
    this.request = request;
    List<Boolean> matchedList = new ArrayList<>();
    for (Map.Entry<String, String> field : recordFields.entrySet()) {
      if(request.patientRecord().containsKey(field.getKey())) {
        matchedList.add(compare(field.getKey(), field.getValue()));
      }
    }
    String matchingRule = algorithmData.getParams().get("matching_rule");
    if (matchingRule.equals("eval_perfect_match"))
      return evalPerfectMatch(matchedList);
    else
      throw new IllegalArgumentException("No Suitable compare Method");
  }

  private boolean compare(String field, String fieldDatabaseValue) {
    String fieldRequestValue = request.patientRecord().get(field);
    if (algorithmData.getFuncs().get(field).equals("feature_match_fuzzy_string"))
      return compareFuzzyString(fieldRequestValue, fieldDatabaseValue);
    if (algorithmData.getFuncs().get(field).equals("feature_match_exact"))
      return compareExactMatch(fieldRequestValue, fieldDatabaseValue);
    else
      throw new IllegalArgumentException("No Suitable compare Method");
  }

  private boolean evalPerfectMatch(List<Boolean> recordScores) {
    return !recordScores.contains(false);
  }

  private boolean compareFuzzyString(String fieldRequestValue, String fieldDatabaseValue) {
//    if (checkIfBothStringsNullOrEmpty(fieldRequestValue, fieldDatabaseValue))
//      return true;
//    String similarityMeasure = algorithmData.getParams().getOrDefault("similarity_measure", "JaroWinkler");
//    Double threshold = Double.parseDouble(algorithmData.getParams().getOrDefault("threshold", ".7"));
//    Double compareScore = StringSimilarity.compareStrings(fieldRequestValue, fieldDatabaseValue, similarityMeasure);
//    return (compareScore >= threshold);
    return true;
  }

  private boolean compareExactMatch(String fieldRequestValue, String fieldDatabaseValue) {
    if (fieldRequestValue != null)
      return fieldRequestValue.equals(fieldDatabaseValue);
    return false;
  }

  private boolean checkIfBothStringsNullOrEmpty(String str1, String str2) {
    if (str1 == null && str2 == null)
      return true;
    return ((str1 != null && str1.isEmpty()) && (str2 != null && str2.isEmpty()));
  }
}
