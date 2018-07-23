package com.flysfo.shorttrips.model.trip;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pierreexygy on 2/29/16.
 */
public class MobileStateInfo implements Serializable {
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String SESSION_ID = "session_id";
    public static final String TRIP_ID = "trip_id";

    Double longitude;
    Double latitude;

    @SerializedName("session_id")
    Integer sessionId;

    @SerializedName("trip_id")
    Integer tripId;

    public MobileStateInfo(Double longitude, Double latitude, Integer sessionId, Integer tripId){
        this.longitude = longitude;
        this.latitude = latitude;
        this.sessionId = sessionId;
        this.tripId = tripId;
    }
}