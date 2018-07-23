package com.flysfo.shorttrips.model.ping;

import android.location.Location;

import com.flysfo.shorttrips.geofence.GeofenceArbiter;
import com.flysfo.shorttrips.trip.TripDateTransform;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Ping implements Serializable {

  public static final String GEOFENCE_STATUS_KEY = "geofence_status";
  public static final String LATITUDE_KEY = "latitude";
  public static final String LONGITUDE_KEY = "longitude";
  public static final String MEDALLION_KEY = "medallion";
  public static final String SESSION_ID_KEY = "session_id";
  public static final String TIMESTAMP_KEY = "timestamp";
  public static final String TRIP_ID_KEY = "trip_id";
  public static final String VEHICLE_ID_KEY = "vehicle_id";

  @SerializedName(GEOFENCE_STATUS_KEY)
  public Integer geofenceStatus;

  public Double latitude;
  public Double longitude;
  public String medallion;

  @SerializedName(SESSION_ID_KEY)
  public Integer sessionId;
  public String timestamp;

  @SerializedName(TRIP_ID_KEY)
  public Integer tripId;

  @SerializedName(VEHICLE_ID_KEY)
  public Integer vehicleId;

  public GeofenceStatus getGeofenceStatus() {
    return GeofenceStatus.fromInt(geofenceStatus);
  }

  private String formattedTimestamp(long utcDate) {
    return TripDateTransform.fromDate(new Date(utcDate));
  }

  public Ping(Location location, Integer tripId, Integer vehicleId, Integer sessionId, String
      medallion) {

    this.geofenceStatus = GeofenceStatus.fromBoolean(GeofenceArbiter
        .checkLocationAgainstTaxiMerged(location)).toInt();
    this.latitude = location.getLatitude();
    this.longitude = location.getLongitude();
    this.medallion = medallion;
    this.sessionId = sessionId;
    this.timestamp = formattedTimestamp(location.getTime());
    this.tripId = tripId;
    this.vehicleId = vehicleId;
  }
}
