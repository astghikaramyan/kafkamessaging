package com.xyz.enterprise.common.util;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * <p>
 * Deserialize string to joda date object using yyyy-MM-dd format. Deserializer
 * is used by json transformer.
 * </p>
 * @author mikalai.kisel@xyz.com
 * @since Mar 08, 2015.
 */
public class DateDeserializer
  implements JsonDeserializer<DateTime>
{

  @Override
  public DateTime deserialize( JsonElement jsonElement, Type type,
    JsonDeserializationContext jsonDeserializationContext )
    throws JsonParseException
  {
    final String dateString = jsonElement.getAsString();
    return DateTimeFormat.forPattern( "yyyy-MM-dd" ).parseDateTime( dateString );
  }
}
