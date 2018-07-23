package com.flysfo.shorttrips.events;

/**
 * Created by mattluedke on 2/12/16.
 */
public class TripValidated {
  public final Integer tripId;

  public TripValidated(Integer tripId) {
    this.tripId = tripId;
  }
}
