package com.flysfo.shorttrips.model.geofence;

import java.io.Serializable;

/**
 * Created by mattluedke on 2/3/16.
 */
public class LocalGeofence implements Serializable {
  public LocalGeofenceFeature[] features;
  public SfoGeofence sfoGeofence;
}
