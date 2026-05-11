package com.xyz.enterprise.fts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FtsResponse {

  private String unwrappedValue;

  public String getUnwrappedValue() {
    return unwrappedValue;
  }
}
