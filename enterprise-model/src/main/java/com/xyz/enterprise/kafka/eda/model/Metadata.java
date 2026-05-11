package com.xyz.enterprise.kafka.eda.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata
{
  private String guid;
  private String version;
  private String correlationId;
  private Integer sequenceId;
  private String type;
  private String source;
  private List<String> destinationWhitelist;
  private List<String> destinationBlacklist;
  private Integer retransmitCount;
  private String generatedTime;
  private String transmissionTime;
  private String contentEncoding;

  public String getGuid()
  {
    return guid;
  }

  public void setGuid( String guid)
  {
    this.guid = guid;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion( String version )
  {
    this.version = version;
  }

  public String getCorrelationId()
  {
    return correlationId;
  }

  public void setCorrelationId( String correlationId )
  {
    this.correlationId = correlationId;
  }

  public String getType()
  {
    return type;
  }

  public void setType( String type )
  {
    this.type = type;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource( String source )
  {
    this.source = source;
  }

  public List<String> getDestinationWhitelist()
  {
    return destinationWhitelist;
  }

  public void setDestinationWhitelist( List<String> destinationWhitelist )
  {
    this.destinationWhitelist = destinationWhitelist;
  }

  public List<String> getDestinationBlacklist()
  {
    return destinationBlacklist;
  }

  public void setDestinationBlacklist( List<String> destinationBlacklist )
  {
    this.destinationBlacklist = destinationBlacklist;
  }

  public Integer getRetransmitCount()
  {
    return retransmitCount;
  }

  public void setRetransmitCount( Integer retransmitCount )
  {
    this.retransmitCount = retransmitCount;
  }

  public String getGeneratedTime()
  {
    return generatedTime;
  }

  public void setGeneratedTime( String generatedTime )
  {
    this.generatedTime = generatedTime;
  }

  public String getTransmissionTime()
  {
    return transmissionTime;
  }

  public void setTransmissionTime( String transmissionTime )
  {
    this.transmissionTime = transmissionTime;
  }

  public Integer getSequenceId()
  {
    return sequenceId;
  }

  public void setSequenceId( Integer sequenceId )
  {
    this.sequenceId = sequenceId;
  }

  public String getContentEncoding()
  {
    return contentEncoding;
  }

  public void setContentEncoding( String contentEncoding )
  {
    this.contentEncoding = contentEncoding;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this);
  }
}
