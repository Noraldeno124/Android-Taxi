package com.flysfo.shorttrips.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mattluedke on 2/9/16.
 */
public class DateTimeDeserializer implements JsonDeserializer<Date> {

  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  @Override
  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US); // "2015-09-03
    // 09:19:20.563"

    Date date = null;
    try {
      date = dateFormat.parse(json.getAsString());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return date;
  }
}