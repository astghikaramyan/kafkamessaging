package com.xyz.enterprise.common.util;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom serializer for ZonedDateTime to have seconds and milliseconds in
 * resulted JSON
 */
public class ZonedDateTimeWithMillisecondsSerializer
  extends
  JsonSerializer<ZonedDateTime>
{

  @Override
  public void serialize( ZonedDateTime value, JsonGenerator jgen, SerializerProvider provider )
    throws IOException, JsonProcessingException
  {
    DateTimeFormatter millisecondsFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss.SSZ" );
    if ( value != null && jgen != null ) {
      jgen.writeString( value.format( millisecondsFormatter ) );
    }
  }
}
