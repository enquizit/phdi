package com.cdc.linkage.utils;

public class Constant {

  public static final String REQUEST_MESSAGE =
      "Request ID:  %s - The following new 'link-record' request was received and will be processed using the 'DIBBS:  %s Algorithm':   %s";

  public static final String RECORDS_PER_ALGORITHM =
      "Request ID: %s - Algorithm ID: %s - Number of records retrieved by the match: %s ";

  public static final String FIELD_RECORDS_COUNT =
      "Request ID :  %s - Field :  %s - Match Criteria :  %s - Match Result :  %s";

  public static final String RECORD_DATA = "Request ID:  %s - Person ID :  %s - Record : -  %s ";

  public static final String NO_TRANSFORMATION = "No transformation";

  public static final String PROCESS_ALGORITHM = "Request ID: %s - Processing Algorithm: %s";

  public static final String CLUSTER_TOTAL =
      "Request ID: %s - PersonId: %s - The cluster total is increased by 1 (Cluster total = %s).";

  public static final String NEW_PERSON =
      "Request ID : %s - No results found. New person and patient records will be created.";

  public static final String NO_OTHER_CANDIDATES =
      "Request ID : %s - Because there are no other candidates, this is the strongest match : %s";

  public static final String STRONGEST_MATCH = "Request ID : %s - the strongest match is :%s ";

  public static final String CURRENT_PERSON =
      "Request ID : %s - Algorithm Result : A new patient record will be created and linked to the existing matched person.";

  public static final String LINKAGE_SCORES = "Request ID : %s - %s ";

  public static final String BELONGING_RATIO_MESSAGE =
      "Request ID :  %s - Person ID : %s  - The cluster ratio is calculated as follows:  Cluster ratio = Cluster total / Cluster records count. =  %s ";

  public static final String CLUSTER_RATIO =
      "Request ID :  %s -  This cluster ratio (' %s ') is greater than the “Cluster Ratio” specified in the algorithm configuration (' %s '). Then the person UUID is added to the candidate linkage scores. ";


  public static final String COMPARE_LOG_ODDS_THRESHOLD =
      "Request ID :  %s" +
          "-Field : %s " +
          "- threshold : %s " +
          "- Values (request record, retrieved record) : %s " +
          "- Comparison score : %s " +
          "- Score, threshold comparison (score < threshold) :%s " +
          "- Final score (score * field’s log-odds) : %s";

  public static final String COMPARE_LOG_ODDS =
      "Request ID :  %s" +
          "-Field : %s " +
          "- Values (request record, retrieved record) : %s " +
          "- Final score  : %s";

  public static final String SCORE_LESS_THAN_TRUE_MATCH =
      "Request ID :  %s - Result:  Not a true match since the sum of scores:  %s " +
          "is less than the provided “true_match_threshold”: %s ";

  public static final String SCORE_GREATER_THAN_TRUE_MATCH =
      "Request ID :  %s - Result:  a true match since the sum of scores:  %s " +
          "is greater than the provided “true_match_threshold”:  %s";

  public static final String SCORE_GREATER_THAN_LOWER_MATCH =
      "Request ID :  %s - Result:  a system potential  match since the sum of scores:  %s " +
          "is greater than the provided “lower_match_threshold”:  %s";

  public static final String SCORE_LESS_THAN_LOWER_MATCH =
      "Request ID :  %s - Result:  Not a system potential  match since the sum of scores:  %s " +
          "is less than the provided “lower_match_threshold”:  %s";

  public static final String SIMPLIFIED_COMPARE_LOG_ODDS =
      "Request ID :  %s " +
          "-person ID :  %s " +
          "-Patient ID :  %s " +
          "-Field : %s " +
          "-Final score  : %s";



}
