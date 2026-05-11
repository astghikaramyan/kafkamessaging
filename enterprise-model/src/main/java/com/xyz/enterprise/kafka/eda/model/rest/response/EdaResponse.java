package com.xyz.enterprise.kafka.eda.model.rest.response;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdaResponse
{

  @JsonProperty("key_schema_id")
  private Integer keySchemaId;
  @JsonProperty("value_schema_id")
  private Integer valueSchemaId;
  @JsonProperty("offsets")
  private List<EdaOffset> offsets;

  public Integer getKeySchemaId()
  {
    return keySchemaId;
  }

  public void setKeySchemaId( Integer keySchemaId )
  {
    this.keySchemaId = keySchemaId;
  }

  public Integer getValueSchemaId()
  {
    return valueSchemaId;
  }

  public void setValueSchemaId( Integer valueSchemaId )
  {
    this.valueSchemaId = valueSchemaId;
  }

  public List<EdaOffset> getOffsets()
  {
    return offsets;
  }

  public void setOffsets( List<EdaOffset> offsets )
  {
    this.offsets = offsets;
  }

  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString( this );
  }
}
