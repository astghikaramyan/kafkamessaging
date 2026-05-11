package com.xyz.enterprise.kafka.eda.model.rest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.ALWAYS)
public class Record<V>
{
  @Size(max = 50)
  private String key;
  @NotNull
  @Valid
  private V value;

  public String getKey()
  {
    return key;
  }

  public void setKey( String key )
  {
    this.key = key;
  }

  public V getValue()
  {
    return value;
  }

  public void setValue( V value )
  {
    this.value = value;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder( "Record{" );
    sb.append( "key='" ).append( key ).append( '\'' );
    sb.append( ", value=" ).append( value );
    sb.append( '}' );
    return sb.toString();
  }
}
