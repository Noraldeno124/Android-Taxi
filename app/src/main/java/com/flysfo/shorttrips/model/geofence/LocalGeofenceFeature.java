package com.flysfo.shorttrips.model.geofence;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattluedke on 2/3/16.
 */
public class LocalGeofenceFeature implements Serializable {

  LocalGeofenceGeometry geometry;

  public List<PolygonInfo> polygonInfos() {

    List<PolygonInfo> polygons = new ArrayList<>();

    for (List<List<Double>> ring : geometry.rings) {
      List<Location> points = new ArrayList<>();

      for (List<Double> point : ring) {
        Location location = new Location("");
        location.setLongitude(point.get(0));
        location.setLatitude(point.get(1)); // for some reason long comes first
        points.add(location);
      }

      polygons.add(new PolygonInfo(points, ringIsClockwise(ring)));
    }

    return polygons;
  }

  private Boolean ringIsClockwise(List<List<Double>> ring) {
    Double clockwisiness = 0.0;

    for (int i = 0; i < ring.size(); i++) {
      List<Double> currendCoord = ring.get(i);
      List<Double> nextCoord = i < ring.size() - 1 ? ring.get(i+1) : ring.get(0);

      clockwisiness += (nextCoord.get(0) - currendCoord.get(0)) * (nextCoord.get(1) +
          currendCoord.get(1));
    }

    return clockwisiness > 0;
  }
}
