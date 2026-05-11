package com.xyz.enterprise.kafka.eda.model.rest.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Severity
{
  ERROR, WARN, FATAL;

  @JsonValue
  public String toValue()
  {
    return name();
  }
}
