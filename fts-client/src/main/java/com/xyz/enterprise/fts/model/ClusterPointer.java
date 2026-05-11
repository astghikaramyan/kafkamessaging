package com.xyz.enterprise.fts.model;

public enum ClusterPointer {
  PRIMARY,
  SECONDARY,
  UNKNOWN;

  /**
   * Returns the ClusterPointer enum constant corresponding to the given string value.
   *
   * @param value the string value to convert
   * @return the corresponding ClusterPointer enum constant, or UNKNOWN if the value does not match any constant
   */
  public static ClusterPointer of(final String value) {
    if (value == null) {
      return ClusterPointer.UNKNOWN;
    }
    try {
      return ClusterPointer.valueOf(value.toUpperCase());
    } catch (Exception e) {
      return ClusterPointer.UNKNOWN;
    }
  }
}
