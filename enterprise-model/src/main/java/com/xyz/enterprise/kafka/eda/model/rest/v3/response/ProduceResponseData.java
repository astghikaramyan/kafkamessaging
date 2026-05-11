package com.xyz.enterprise.kafka.eda.model.rest.v3.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProduceResponseData {
  private String type;
  private Integer size;

  public ProduceResponseData() {
  }

  public String getType() {
    return this.type;
  }

  public Integer getSize() {
    return this.size;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setSize(Integer size) {
    this.size = size;
  }


  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString( this );
  }


}
