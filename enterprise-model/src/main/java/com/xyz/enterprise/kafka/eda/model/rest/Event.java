package com.xyz.enterprise.kafka.eda.model.rest;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.xyz.enterprise.kafka.eda.model.Context;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event
{
  @Size(min = 20, max = 50)
  private String guid;
  @NotNull
  @Size(min = 3, max = 100)
  private String topicName;
  private Integer partitionNum;
  private Long offset;
  @NotNull
  private String message;
  private JsonNode jsonMessage;
  private List<Context> context;

  public List<Context> getContext()
  {
    return context;
  }

  public void setContext( List<Context> context )
  {
    this.context = context;
  }
  public String getGuid()
  {
    return guid;
  }

  public void setGuid( String guid )
  {
    this.guid = guid;
  }

  public String getTopicName()
  {
    return topicName;
  }

  public void setTopicName( String topicName )
  {
    this.topicName = topicName;
  }

  public Integer getPartitionNum()
  {
    return partitionNum;
  }

  public void setPartitionNum( Integer partitionNum )
  {
    this.partitionNum = partitionNum;
  }

  public Long getOffset()
  {
    return offset;
  }

  public void setOffset( Long offset )
  {
    this.offset = offset;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage( String message )
  {
    this.message = message;
  }

  public JsonNode getJsonMessage() {
    return jsonMessage;
  }

  public void setJsonMessage(JsonNode jsonMessage) {
    this.jsonMessage = jsonMessage;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this);
  }
}
