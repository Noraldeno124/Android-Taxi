package com.flysfo.shorttrips.geofence;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

import com.flysfo.shorttrips.model.geofence.GeofenceVars;
import com.flysfo.shorttrips.model.geofence.LocalGeofence;
import com.flysfo.shorttrips.model.geofence.LocalGeofenceFeature;
import com.flysfo.shorttrips.model.geofence.PolygonInfo;
import com.flysfo.shorttrips.model.geofence.SfoGeofence;

import java.util.ArrayList;
import java.util.List;

public class GeofenceArbiter {

  static void requestGeofencesForLocation(final Activity activity,
                                          final Location location,
                                          final GeofenceResponder responder) {

    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {

        final List<SfoGeofence> validGeofences = new ArrayList<>();

        for (LocalGeofence localGeofence : GeofenceVars.getInstance(activity).getAllGeofences()) {
          if (checkLocationAgainstFeatures(location, localGeofence.features)) {
            validGeofences.add(localGeofence.sfoGeofence);
          }
        }

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            responder.geofencesFound(validGeofences);
          }
        });
      }
    });
  }

  public static Boolean checkLocationAgainstTaxiMerged(Location location) {
    return checkLocationAgainstFeatures(location, GeofenceVars.getInstance()
        .getTaxiMergedGeofence().features);
  }

  public static Boolean checkLocationAgainstFeatures(Location location, LocalGeofenceFeature[]
      features) {

    for (LocalGeofenceFeature feature : features) {
      if (locationSatisfiesPolygonInfos(location, feature.polygonInfos())) {
        return true;
      }
    }

    return false;
  }

  private static Boolean locationSatisfiesPolygonInfos(Location location, List<PolygonInfo>
      polygonInfos) {

    for (PolygonInfo polygonInfo : polygonInfos) {
      if (polygonInfo.additive != locationIsInsideRegion(location, polygonInfo.polygon)) {
        return false;
      }
    }

    return true;
  }

  // thanks to:
  // http://stackoverflow.com/a/26030795/2577986
  // http://stackoverflow.com/a/18486861/2577986
  private static Boolean locationIsInsideRegion(Location location, List<Location> region) {

    int intersectCount = 0;
    for (int j = 0; j < region.size() - 1; j++) {
      if (rayCastIntersect(location, region.get(j), region.get(j + 1))) {
        intersectCount++;
      }
    }

    return ((intersectCount % 2) == 1); // odd = inside, even = outside;
  }

  private static boolean rayCastIntersect(Location tap, Location vertA, Location vertB) {

    double aY = vertA.getLatitude();
    double aX = vertA.getLongitude();

    double bY = vertB.getLatitude();
    double bX = vertB.getLongitude();

    double pY = tap.getLatitude();
    double pX = tap.getLongitude();

    if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
        || (aX < pX && bX < pX)) {
      return false; // a and b can't both be above or below pt.y, and a or
      // b must be east of pt.x
    }

    double m = (aY - bY) / (aX - bX); // Rise over run
    double bee = (-aX) * m + aY; // y = mx + b
    double x = (pY - bee) / m; // algebra is neat!

    return x > pX;
  }
}
