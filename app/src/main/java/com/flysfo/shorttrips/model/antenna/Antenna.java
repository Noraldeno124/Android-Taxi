package com.flysfo.shorttrips.model.antenna;

import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mattluedke on 2/10/16.
 */
public class Antenna implements Serializable {

  @SerializedName("device_id")
  String antennaId;

  @SerializedName("device_location")
  String aviLocation;

  @SerializedName("time_read")
  public Date aviDate;

  public GtmsLocation device() {
    return GtmsLocation.fromAviId(antennaId);
  }
}
