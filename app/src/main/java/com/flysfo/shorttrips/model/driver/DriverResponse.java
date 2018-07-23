package com.flysfo.shorttrips.model.driver;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by mattluedke on 12/16/15.
 */
public class DriverResponse implements Serializable {
  public Driver response;

  public class Driver implements Serializable {
    @SerializedName("card_id")
    public String cardId;
    @SerializedName("driver_id")
    public Integer driverId;
    @SerializedName("driver_license")
    String driverLicense;
    @SerializedName("first_name")
    String firstName;
    @SerializedName("last_name")
    String lastName;
    @SerializedName("session_id")
    public Integer sessionId;
  }
}
