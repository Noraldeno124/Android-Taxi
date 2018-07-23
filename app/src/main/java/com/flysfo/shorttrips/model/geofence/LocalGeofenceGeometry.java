package com.flysfo.shorttrips.model.geofence;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mattluedke on 2/3/16.
 */
public class LocalGeofenceGeometry implements Serializable {
  List<List<List<Double>>> rings;
}
