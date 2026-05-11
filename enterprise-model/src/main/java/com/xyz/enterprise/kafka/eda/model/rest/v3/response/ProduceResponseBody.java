package com.xyz.enterprise.kafka.eda.model.rest.v3.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProduceResponseBody {

  @JsonProperty("error_code")
  private Integer errorCode;
  private String message;
  @JsonProperty("cluster_id")
  private String clusterId;
  @JsonProperty("topic_name")
  private String topicName;
  @JsonProperty("partition_id")
  private Integer partitionId;
  private Long offset;
  private String timestamp;
  private ProduceResponseData key;
  private ProduceResponseData value;

  public Integer getErrorCode() {
    return this.errorCode;
  }

  public String getMessage() {
    return this.message;
  }

  public String getClusterId() {
    return this.clusterId;
  }

  public String getTopicName() {
    return this.topicName;
  }

  public Integer getPartitionId() {
    return this.partitionId;
  }

  public Long getOffset() {
    return this.offset;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public ProduceResponseData getKey() {
    return this.key;
  }

  public ProduceResponseData getValue() {
    return this.value;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public void setPartitionId(Integer partitionId) {
    this.partitionId = partitionId;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public void setKey(ProduceResponseData key) {
    this.key = key;
  }

  public void setValue(ProduceResponseData value) {
    this.value = value;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString( this );
  }


}
