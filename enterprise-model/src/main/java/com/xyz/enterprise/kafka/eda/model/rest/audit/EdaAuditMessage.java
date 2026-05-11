package com.xyz.enterprise.kafka.eda.model.rest.audit;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.xyz.enterprise.kafka.eda.model.Context;
import com.xyz.enterprise.kafka.eda.model.rest.Action;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdaAuditMessage
{
  @NotNull
  @Size(min = 20, max = 50)
  private String guid;
  @NotNull
  private String auditSource;
  @NotNull
  private String auditTransmitTime;
  @NotNull
  private Action action;
  @NotNull
  private String topicName;
  @NotNull
  private String type;
  @NotNull
  private String amaRequestId;
  @NotNull
  @Size(min = 3, max = 3)
  private String statusCode;
  @NotNull
  private String statusDesc;
  @NotNull
  private String statusMessage;
  private Integer partitionNum;
  private Long offset;
  @NotNull
  private String hotelCode;
  @Size(min = 20, max = 50)
  private String correlationId;
  private Integer sequenceId;
  @NotNull
  @Size(min = 1, max = 10)
  private String version;
  @NotNull
  @Size(min = 1, max = 50)
  private String source;
  @Size(max = 20)
  private List<String> destinationWhitelist;
  @Size(max = 20)
  private List<String> destinationBlacklist;
  private Number retransmitCount;
  @NotNull
  private String generatedTime;
  @NotNull
  private String transmissionTime;
  private String message;
  private JsonNode jsonMessage;
  private List<Context> context;

  private String consumerWid;

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

  public String getAuditSource()
  {
    return auditSource;
  }

  public void setAuditSource( String auditSource )
  {
    this.auditSource = auditSource;
  }

  public String getAuditTransmitTime()
  {
    return auditTransmitTime;
  }

  public void setAuditTransmitTime( String auditTransmitTime )
  {
    this.auditTransmitTime = auditTransmitTime;
  }

  public Action getAction()
  {
    return action;
  }

  public void setAction( Action action )
  {
    this.action = action;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAmaRequestId() {
    return amaRequestId;
  }

  public void setAmaRequestId(String amaRequestId) {
    this.amaRequestId = amaRequestId;
  }

  public String getStatusCode()
  {
    return statusCode;
  }

  public void setStatusCode( String statusCode )
  {
    this.statusCode = statusCode;
  }

  public String getStatusDesc()
  {
    return statusDesc;
  }

  public void setStatusDesc( String statusDesc )
  {
    this.statusDesc = statusDesc;
  }

  public String getStatusMessage()
  {
    return statusMessage;
  }

  public void setStatusMessage( String statusMessage )
  {
    this.statusMessage = statusMessage;
  }

  public Integer getPartitionNum() {
    return partitionNum;
  }

  public void setPartitionNum(Integer partitionNum) {
    this.partitionNum = partitionNum;
  }

  public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public Integer getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(Integer sequenceId) {
    this.sequenceId = sequenceId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public List<String> getDestinationWhitelist() {
    return destinationWhitelist;
  }


  public String getConsumerWid() { return consumerWid; }

  public void setConsumerWid(String consumerWid) { this.consumerWid = consumerWid; }

  public void setDestinationWhitelist(List<String> destinationWhitelist) {
    this.destinationWhitelist = destinationWhitelist;
  }

  public List<String> getDestinationBlacklist() {
    return destinationBlacklist;
  }

  public void setDestinationBlacklist(List<String> destinationBlacklist) {
    this.destinationBlacklist = destinationBlacklist;
  }

  public Number getRetransmitCount() {
    return retransmitCount;
  }

  public void setRetransmitCount(Number retransmitCount) {
    this.retransmitCount = retransmitCount;
  }

  public String getGeneratedTime() {
    return generatedTime;
  }

  public void setGeneratedTime(String generatedTime) {
    this.generatedTime = generatedTime;
  }

  public String getTransmissionTime() {
    return transmissionTime;
  }

  public void setTransmissionTime(String transmissionTime) {
    this.transmissionTime = transmissionTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public JsonNode getJsonMessage() {
    return jsonMessage;
  }

  public void setJsonMessage(JsonNode jsonMessage) {
    this.jsonMessage = jsonMessage;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
