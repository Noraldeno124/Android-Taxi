package com.flysfo.shorttrips.model.trip;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mattluedke on 2/11/16.
 */
public class TripIdResponse implements Serializable {
  public TripId response;

  public class TripId implements Serializable {
    @SerializedName("trip_id")
    public Integer tripId;
  }
}
