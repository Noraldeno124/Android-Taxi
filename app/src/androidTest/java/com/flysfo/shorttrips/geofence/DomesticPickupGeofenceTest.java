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
public class DomesticPickupGeofenceTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public DomesticPickupGeofenceTest() {
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

  public void testDomesticPickupGeofence() {
    LocalGeofence geofence = GeofenceVars.getInstance(mainActivity).getDomesticPickupGeofence();

    assertNotNull(geofence);

    assertTrue(geofence.sfoGeofence == SfoGeofence.SFO_TAXI_DOMESTIC_EXIT);

    Location badPoint = new Location("");
    badPoint.setLatitude(37.617515);
    badPoint.setLongitude(-122.398274);

    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(badPoint, geofence.features));

    Location goodPoint = new Location("");
    goodPoint.setLatitude(37.614938);
    goodPoint.setLongitude(-122.386390);

    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(goodPoint, geofence.features));
  }
}
