package com.xyz.enterprise.offer.event.feed.model.eda;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xyz.enterprise.kafka.eda.model.EdaMessage;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Record {

  private String key;
  private EdaMessage value;
}
