package com.flysfo.shorttrips.model.queue;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pierreexygy on 2/29/16.
 */
public class QueueLength implements Serializable {
  @SerializedName("long_queue_length")
  Integer longQueueLength;

  @SerializedName("short_queue_length")
  Integer shortQueueLength;

  public Integer getLongQueueLength() {
    return longQueueLength;
  }
}