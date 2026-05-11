package com.xyz.enterprise.offer.event.feed.model.eda;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EdaProxyRequest {

  private List<Record> records;

  public EdaProxyRequest() {
  }
}
