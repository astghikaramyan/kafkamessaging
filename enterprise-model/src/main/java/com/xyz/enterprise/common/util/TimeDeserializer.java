package com.xyz.enterprise.common.util;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


/**
 * <p>
 * Deserialize string to joda time object using HH:mm:ss format. Deserializer is
 * used by json transformer.
 * </p>
 * @author mikalai.kisel@xyz.com
 * @since Mar 08, 2015.
 */
public class TimeDeserializer
  implements JsonDeserializer<LocalTime>
{
  @Override
  public LocalTime deserialize( JsonElement jsonElement, Type type,
    JsonDeserializationContext jsonDeserializationContext )
    throws JsonParseException
  {
    final String timeString = jsonElement.getAsString();
    DateTime dateTime = DateTimeFormat.forPattern( "HH:mm:ss" ).parseDateTime(
      timeString );

    return new LocalTime(
      dateTime.getHourOfDay(), dateTime.getMinuteOfHour(),
      dateTime.getMillisOfSecond() );
  }
}
