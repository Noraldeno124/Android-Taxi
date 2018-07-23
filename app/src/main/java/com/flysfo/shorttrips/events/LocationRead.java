package com.flysfo.shorttrips.events;

import android.location.Location;

/**
 * Created by mattluedke on 2/8/16.
 */
public class LocationRead {
  public final Location location;

  public LocationRead(Location location) {
    this.location = location;
  }
}
