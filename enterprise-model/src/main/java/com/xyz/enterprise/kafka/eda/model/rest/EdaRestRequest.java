package com.xyz.enterprise.kafka.eda.model.rest;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdaRestRequest<T>
{
  @Size(min = 1, max = 15)
  @NotNull
  private List<Record<T>> records;

  public List<Record<T>> getRecords()
  {
    return records;
  }

  public void setRecords( List<Record<T>> records )
  {
    this.records = records;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder( "EdaRestRequest{" );
    sb.append( "records=" ).append( records );
    sb.append( '}' );
    return sb.toString();
  }
}
