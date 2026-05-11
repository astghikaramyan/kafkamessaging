package com.xyz.enterprise.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Type;

public class XmlGregorianCalendarSerializer
  implements JsonSerializer<XMLGregorianCalendar>
{
  private final String defaultDatePrefix = "1970-01-01T";
  private final String timePattern = "\\d{2}:\\d{2}:\\d{2}";

  public XmlGregorianCalendarSerializer()
  {
  }

  @Override
  public JsonElement serialize( XMLGregorianCalendar xmlGregorianCalendar, Type type,
    JsonSerializationContext jsonSerializationContext )
  {
    final String value = xmlGregorianCalendar.toString();

    if ( value.matches( timePattern ) ) {
      return new JsonPrimitive( defaultDatePrefix + value + "Z" );
    }

    return new JsonPrimitive( value );
  }
}