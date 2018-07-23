package com.flysfo.shorttrips.model.geofence;

import android.location.Location;

import java.util.List;

/**
 * Created by mattluedke on 2/3/16.
 */
public class PolygonInfo {
  public List<Location> polygon;
  public Boolean additive;

  PolygonInfo(List<Location> polygon, Boolean additive) {
    this.polygon = polygon;
    this.additive = additive;
  }
}
