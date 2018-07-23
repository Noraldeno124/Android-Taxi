package com.flysfo.shorttrips.model.ping;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PingBatch implements Serializable {

  public List<Ping> pings;

  @SerializedName("session_id")
  public Integer sessionId;

  public PingBatch(List<Ping> pings, Integer sessionId) {
    this.pings = new ArrayList<>(pings);
    this.sessionId = sessionId;
  }
}
