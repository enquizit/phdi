package com.cdc.linkage.utils;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

public class StringSimilarity {


  public static double compareStrings(String string1, String string2, String similarityMeasure) {
    switch (similarityMeasure) {
      case "JaroWinkler":
        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        return jaroWinkler.apply(string1, string2);
      case "Levenshtein":
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        int maxLen = Math.max(string1.length(), string2.length());
        return 1.0 - (double) levenshtein.apply(string1, string2) / maxLen;
      case "DamerauLevenshtein":
        StringMetric damerauLevenshtein = StringMetrics.damerauLevenshtein();
        return damerauLevenshtein.compare(string1, string2);
      default:
        throw new IllegalArgumentException("Unknown similarity measure: " + similarityMeasure);
    }
  }


}
