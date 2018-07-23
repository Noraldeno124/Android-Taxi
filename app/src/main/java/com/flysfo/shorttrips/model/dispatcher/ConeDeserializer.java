package com.flysfo.shorttrips.model.dispatcher;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConeDeserializer implements JsonDeserializer<Cone> {

  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  @Override
  public Cone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    Cone cone = new Cone();
    try {
      JsonObject jsonObject = json.getAsJsonObject();
      cone.isConed = jsonObject.get("is_coned").getAsBoolean();
      cone.lastUpdated = dateFormat.parse(jsonObject.getAsJsonPrimitive("last_updated").getAsString());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return cone;
  }
}
