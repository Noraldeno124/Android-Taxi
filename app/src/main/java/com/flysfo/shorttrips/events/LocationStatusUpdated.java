package com.flysfo.shorttrips.events;

/**
 * Created by mattluedke on 3/3/16.
 */
public class LocationStatusUpdated {
  public final Boolean locationActive;

  public LocationStatusUpdated(Boolean locationActive) {
    this.locationActive = locationActive;
  }
}
