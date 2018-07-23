package com.flysfo.shorttrips.events;

import com.flysfo.shorttrips.model.geofence.SfoGeofence;

import java.util.List;

/**
 * Created by mattluedke on 2/8/16.
 */
public class FoundInsideGeofences {
  public final List<SfoGeofence> geofences;

  public FoundInsideGeofences(List<SfoGeofence> geofences) {
    this.geofences = geofences;
  }
}
