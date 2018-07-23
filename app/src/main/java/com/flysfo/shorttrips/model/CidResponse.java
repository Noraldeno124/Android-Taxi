package com.flysfo.shorttrips.model;

import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mattluedke on 2/8/16.
 */
public class CidResponse implements Serializable {
  public Cid response;

  public class Cid implements Serializable {
    @SerializedName("device_id")
    String cidId;

    @SerializedName("device_location")
    String cidLocation;

    @SerializedName("time_read")
    public Date cidTimeRead;

    public GtmsLocation device() {
      return GtmsLocation.fromCidId(cidId);
    }
  }
}
