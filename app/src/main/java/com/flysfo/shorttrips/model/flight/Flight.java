package com.flysfo.shorttrips.model.flight;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mattluedke on 2/10/16.
 */
public class Flight implements Serializable {
    @SerializedName("airline_name")
    String airline;

    @SerializedName("bags")
    int bags;

    @SerializedName("estimated_time")
    Date estimatedTime;

    @SerializedName("flight_number")
    String flightNumber;

    @SerializedName("remarks")
    FlightStatus flightStatus;

    @SerializedName("scheduled_time")
    Date scheduledTime;

    public String getAirline(){
      return airline;
    }
    public int getBags(){
      return bags;
    }
    public Date getEstimatedTime(){
      return estimatedTime;
    }
    public Date getScheduledTime(){
      return scheduledTime;
    }
    public String getFlightNumber(){
      return flightNumber;
    }
    public FlightStatus getFlightStatus(){
      return flightStatus;
    }
    public String getIataCode(){
      return flightNumber.substring(0, 2);
    }
}
