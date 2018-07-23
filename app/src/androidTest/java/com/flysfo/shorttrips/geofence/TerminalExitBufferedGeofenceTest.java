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
public class TerminalExitBufferedGeofenceTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public TerminalExitBufferedGeofenceTest() {
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

  public void testTerminalExitBufferedGeofence() {
    LocalGeofence geofence = GeofenceVars.getInstance(mainActivity).getTerminalExitBufferedGeofence();

    assertNotNull(geofence);

    assertTrue(geofence.sfoGeofence == SfoGeofence.TAXI_EXIT_BUFFERED);

    Location badPoint = new Location("");
    badPoint.setLatitude(37.617729);
    badPoint.setLongitude(-122.398403);

    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(badPoint, geofence.features));

    Location goodPoint = new Location("");
    goodPoint.setLatitude(37.614695);
    goodPoint.setLongitude(-122.39468);

    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(goodPoint, geofence.features));
  }
}
