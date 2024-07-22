package com.cdc.linkage.utils;

public class StringUtil {

  private StringUtil() {//All methods are static,  public constructor is meaningless
  }

  public static boolean isBlankOrNull(String val) {
    return (val == null || val.isBlank());
  }
}
