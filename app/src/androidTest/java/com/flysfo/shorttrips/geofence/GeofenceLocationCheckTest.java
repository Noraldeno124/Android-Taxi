package com.flysfo.shorttrips.geofence;

import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.geofence.SfoGeofence;
import com.flysfo.shorttrips.networking.Url;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by mattluedke on 10/26/16.
 */

public class GeofenceLocationCheckTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public GeofenceLocationCheckTest() {
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

  public void testPoints() throws InterruptedException {

    // good point
    Location goodLocation = new Location("");
    goodLocation.setLatitude(37.614938);
    goodLocation.setLongitude(-122.386390);

    final CountDownLatch goodWaiter = new CountDownLatch(1);
    final List<SfoGeofence> goodGeofenceList = new ArrayList<>();

    GeofenceArbiter.requestGeofencesForLocation(mainActivity, goodLocation, new GeofenceResponder() {
      @Override
      public void geofencesFound(List<SfoGeofence> geofences) {
        goodGeofenceList.addAll(geofences);
        goodWaiter.countDown();
      }
    });

    goodWaiter.await(10, TimeUnit.SECONDS);
    assertTrue(goodGeofenceList.size() > 0);

    // bad point
    Location badLocation = new Location("");
    badLocation.setLatitude(36.614938);
    badLocation.setLongitude(-121.386390);

    final CountDownLatch badWaiter = new CountDownLatch(1);
    final List<SfoGeofence> badGeofenceList = new ArrayList<>();

    GeofenceArbiter.requestGeofencesForLocation(mainActivity, badLocation, new GeofenceResponder() {
      @Override
      public void geofencesFound(List<SfoGeofence> geofences) {
        badGeofenceList.addAll(geofences);
        badWaiter.countDown();
      }
    });

    badWaiter.await(10, TimeUnit.SECONDS);
    assertTrue(badGeofenceList.size() == 0);
  }
}
