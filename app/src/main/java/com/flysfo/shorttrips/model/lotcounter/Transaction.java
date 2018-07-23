package com.flysfo.shorttrips.model.lotcounter;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pierreexygy on 12/12/17.
 */

public class Transaction implements Serializable {

  public static class Fields {
    public static final String CLIENT_SESSION_ID = "client_session_id";
    public static final String DRIVER_CARD_ID = "driver_card_id";
    public static final String EVENT_TYPE_ID= "event_type_id";
    public static final String TRIP_TYPE= "trip_type";
    public static final String STATUS= "status";
  }

  public Transaction(Integer clientSessionId, String driverCardId, Integer eventTypeId, String tripType, Integer status) {
    this.clientSessionId = clientSessionId;
    this.driverCardId = driverCardId;
    this.eventTypeId = eventTypeId;
    this.tripType = tripType;
    this.status = status;
  }

  @SerializedName("client_session_id")
  public Integer clientSessionId;

  @SerializedName("driver_card_id")
  public String driverCardId;

  @SerializedName("event_type_id")
  public Integer eventTypeId;

  @SerializedName("trip_type")
  public String tripType;

  @SerializedName("status")
  public Integer status;
}
