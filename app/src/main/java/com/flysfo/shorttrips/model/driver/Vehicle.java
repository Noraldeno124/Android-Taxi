package com.flysfo.shorttrips.model.driver;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mattluedke on 2/10/16.
 */
public class Vehicle implements Serializable {

  public Vehicle(Integer gtmsTripId, String licensePlate, String medallion, Integer
      transponderId, Integer vehicleId) {
    this.gtmsTripId = gtmsTripId;
    this.licensePlate = licensePlate;
    this.medallion = medallion;
    this.transponderId = transponderId;
    this.vehicleId = vehicleId;
  }

  @SerializedName("gtms_trip_id")
  Integer gtmsTripId;

  @SerializedName("license_plate")
  String licensePlate;

  @SerializedName("medallion")
  public String medallion;

  @SerializedName("transponder_id")
  public Integer transponderId;

  @SerializedName("vehicle_id")
  public Integer vehicleId;

  public Boolean isValid() {
    return gtmsTripId != null
        && licensePlate != null
        && medallion != null
        && transponderId != null
        && vehicleId != null;
  }
}
