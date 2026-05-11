package com.xyz.enterprise.kafka.eda.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EdaMessage
{
  private Metadata metadata;

  private List<Context> context;

  private String message;

  public String findValueInContext(String key) {
    return Optional.ofNullable(context).map(List::stream).orElseGet(Stream::empty)
        .filter(item -> key.equalsIgnoreCase(item.getKey())).findFirst().map(Context::getValue).orElse(null);
  }

  public Metadata getMetadata()
  {
    return metadata;
  }

  public void setMetadata( Metadata metadata )
  {
    this.metadata = metadata;
  }

  public List<Context> getContext()
  {
    return context;
  }

  public void setContext( List<Context> context )
  {
    this.context = context;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage( String message )
  {
    this.message = message;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this);
  }
}
