package com.xyz.enterprise.kafka.eda.model.rest.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdaOffset
{

  @JsonProperty("partition")
  private Integer partition;
  @JsonProperty("offset")
  private Long offset;
  @JsonProperty("error_code")
  private Long errorCode;
  @JsonProperty("error")
  private String error;

  public Integer getPartition()
  {
    return partition;
  }

  public void setPartition( Integer partition )
  {
    this.partition = partition;
  }

  public Long getOffset()
  {
    return offset;
  }

  public void setOffset( Long offset )
  {
    this.offset = offset;
  }

  public Long getErrorCode()
  {
    return errorCode;
  }

  public void setErrorCode( Long errorCode )
  {
    this.errorCode = errorCode;
  }

  public String getError()
  {
    return error;
  }

  public void setError( String error )
  {
    this.error = error;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString( this );
  }
}
