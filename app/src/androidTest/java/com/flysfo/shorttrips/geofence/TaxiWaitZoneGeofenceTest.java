package com.flysfo.shorttrips.geofence;

import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.geofence.GeofenceVars;
import com.flysfo.shorttrips.model.geofence.LocalGeofence;
import com.flysfo.shorttrips.model.geofence.SfoGeofence;
import com.flysfo.shorttrips.networking.Url;

/**
 * Created by mattluedke on 2/3/16.
 */
public class TaxiWaitZoneGeofenceTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public TaxiWaitZoneGeofenceTest() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!Url.isStaging()) {
      throw new RuntimeException("can't use production URL in tests");
    }

    mainActivity = getActivity();
  }

  public void testTaxiWaitZoneGeofence() {
    LocalGeofence geofence = GeofenceVars.getInstance(mainActivity).getTaxiWaitingZone();

    assertNotNull(geofence);

    assertTrue(geofence.sfoGeofence == SfoGeofence.TAXI_WAITING_ZONE);

    Location badPoint = new Location("");
    badPoint.setLatitude(37.614938);
    badPoint.setLongitude(-122.386390);

    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(badPoint, geofence.features));

    Location goodPoint = new Location("");
    goodPoint.setLatitude(37.616307);
    goodPoint.setLongitude(-122.386158);

    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(goodPoint, geofence.features));
  }
}
