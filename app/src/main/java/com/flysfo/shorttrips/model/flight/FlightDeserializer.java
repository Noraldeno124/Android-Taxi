package com.flysfo.shorttrips.model.flight;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pierreexygy on 3/16/16.
 */
public class FlightDeserializer implements JsonDeserializer<Flight> {

  private static final String DATE_FORMAT = "hh:mm a"; // "2:50 PM"

  @Override
  public Flight deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
      JsonParseException {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US); // "2:50 PM"

    Flight flight = new Flight();
    try {
      JsonObject jsonObject = json.getAsJsonObject();
      flight.airline = jsonObject.get("airline_name").getAsString();
      flight.flightNumber = jsonObject.get("flight_number").getAsString();

      flight.flightStatus = FlightStatus.fromString( jsonObject.get("remarks").getAsString() );

      flight.bags = jsonObject.get("bags").getAsInt();
      flight.estimatedTime = dateFormat.parse(jsonObject.getAsJsonPrimitive("estimated_time").getAsString());
      flight.scheduledTime = dateFormat.parse(jsonObject.getAsJsonPrimitive("scheduled_time").getAsString());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return flight;
  }

  public static String flightDateToString(Date date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US); // "2:50 PM"
    return dateFormat.format(date);
  }
}