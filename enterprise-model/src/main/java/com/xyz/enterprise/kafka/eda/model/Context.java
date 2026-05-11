package com.xyz.enterprise.kafka.eda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context
{
  private String key;
  private String value;

  public static Context of( String key, String value ) {
    Context context = new Context();
    context.setKey( key );
    context.setValue( value );
    return context;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey( String key )
  {
    this.key = key;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue( String value )
  {
    this.value = value;
  }

  @Override
  public String toString()
  {
    return "{" +
      "key: '" + key + '\'' +
      ", value: '" + value + '\'' +
      '}';
  }
}
