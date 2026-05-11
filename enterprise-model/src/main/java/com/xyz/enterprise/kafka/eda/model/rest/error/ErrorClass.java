package com.xyz.enterprise.kafka.eda.model.rest.error;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorClass
{
  KAFKA( "Kafka" ), APPLICATION( "Application" ), SYSTEM( "System" );

  String value;

  ErrorClass( String value )
  {
    this.value = value;
  }

  @JsonValue
  public String toValue()
  {
    return value;
  }

  @Override
  public String toString()
  {
    return value;
  }
}
