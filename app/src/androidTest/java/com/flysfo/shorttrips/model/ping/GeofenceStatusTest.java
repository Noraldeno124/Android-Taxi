package com.flysfo.shorttrips.model.ping;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.networking.Url;
import com.flysfo.shorttrips.util.Util;

/**
 * Created by mattluedke on 10/26/16.
 */

public class GeofenceStatusTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public GeofenceStatusTest() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!Url.isStaging()) {
      throw new RuntimeException("can't use production URL in tests");
    }

    mainActivity = getActivity();
    Util.TESTING = true;
  }

  public void testGeofenceStatus() {
    GeofenceStatus status1 = GeofenceStatus.fromBoolean(true);
    assertTrue(status1 == GeofenceStatus.VALID);
    assertTrue(status1.toBool());

    GeofenceStatus status2 = GeofenceStatus.fromBoolean(false);
    assertTrue(status2 == GeofenceStatus.INVALID);
    assertFalse(status2.toBool());

    GeofenceStatus status3 = GeofenceStatus.fromInt(0);
    assertTrue(status3 == GeofenceStatus.INVALID);
    assertTrue(status3.toInt() == 0);

    GeofenceStatus status4 = GeofenceStatus.fromInt(1);
    assertTrue(status4 == GeofenceStatus.VALID);
    assertTrue(status4.toInt() == 1);

    GeofenceStatus status5 = GeofenceStatus.fromInt(-1);
    assertTrue(status5 == GeofenceStatus.NOT_VERIFIED);
    assertTrue(status5.toInt() == -1);
  }
}
