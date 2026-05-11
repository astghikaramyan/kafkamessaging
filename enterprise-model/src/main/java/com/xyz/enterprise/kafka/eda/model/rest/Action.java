package com.xyz.enterprise.kafka.eda.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Action
{
  PRODUCE( "Produce" ), CONSUME( "Consume" ), MESSAGE( "Message" ), PROCESS( "Process" );

  String value;

  Action( String value )
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
