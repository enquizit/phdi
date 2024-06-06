package com.cdc.linkage.utils;

public class Constant {

  public static final String REQUEST_MESSAGE  = "Request ID: {} - The following new 'link-record' request was received " +
      "and will be processed using the 'DIBBS: {} Algorithm':  {}";

  public static final String RECORDS_PER_ALGORITHM  =  "Request ID: {} - Number of records retrieved by the match: {} ";

  public static final String PERSON_BELONG_RATIO  ="Person ID : {} - belongingness_ratio =======>>>>>>>>>> :{}";

  public static final String START_FINDING_PERSON_SCORE = "Starting _find_strongest_link";
  public static final String PERSON_FINAL_SCORE ="Request ID: {} - Match found! Person ID:{}  with score: {}";

  public static final String FIELD_RECORDS_COUNT  ="Request ID : {} - Field : {} - Match Criteria : {} - Match Result : {}";

  public static final String FIELD_NOT_IN_REQUEST  ="Field : {} - transformation : {} Ignored (not included in the request)";

  public static final String RECORD_DATA  ="Request ID: {} - Person ID : {} - Record : - {} ";


}
