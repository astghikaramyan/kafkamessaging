package com.xyz.enterprise.fts.model;

public enum RegionPointer {
  ALL,
  NONE,
  LOCAL,
  US_EAST_1,
  US_WEST_1,
  EU_WEST_2,
  AP_SOUTHEAST_1,
  DC4,
  SC9,
  UNKNOWN;

  /**
   * Converts a string to a RegionPointer enum.
   *
   * @param value the string value to convert
   * @return the corresponding RegionPointer enum, or UNKNOWN if the value is null or invalid
   */
  public static RegionPointer of(final String value) {
    if (value == null) {
      return RegionPointer.UNKNOWN;
    }
    try {
      return RegionPointer.valueOf(value.toUpperCase());
    } catch (Exception e) {
      return RegionPointer.UNKNOWN;
    }
  }
}
