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
public class TaxiMergedGeofenceTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public TaxiMergedGeofenceTest() {
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

  public void testSfoGeofence() {
    LocalGeofence geofence = GeofenceVars.getInstance(mainActivity).getTaxiMergedGeofence();

    assertNotNull(geofence);

    assertTrue(geofence.sfoGeofence == SfoGeofence.TAXI_SFO_MERGED);

    // In SF
    Location sfPoint = new Location("");
    sfPoint.setLatitude(37.752598);
    sfPoint.setLongitude(-122.415504);
    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(sfPoint, geofence.features));

    // Palo Alto
    Location paloAltoPoint = new Location("");
    paloAltoPoint.setLatitude(37.438202);
    paloAltoPoint.setLongitude(-122.154922);
    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(paloAltoPoint, geofence.features));

    // Brisbane
    Location brisbanePoint = new Location("");
    brisbanePoint.setLatitude(37.681205);
    brisbanePoint.setLongitude(-122.400570);
    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(brisbanePoint, geofence.features));

    // San Mateo
    Location sanMateoPoint = new Location("");
    sanMateoPoint.setLatitude(37.570909);
    sanMateoPoint.setLongitude(-122.317486);
    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(sanMateoPoint, geofence.features));

    // Good Belmont point
    Location goodBelmontPoint = new Location("");
    goodBelmontPoint.setLatitude(37.517546);
    goodBelmontPoint.setLongitude(-122.286308);
    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(goodBelmontPoint, geofence.features));

    // Bad Belmont point
    Location badBelmontPoint = new Location("");
    badBelmontPoint.setLatitude(37.505563);
    badBelmontPoint.setLongitude(-122.289612);
    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(badBelmontPoint, geofence.features));

    // Skyline College
    Location skylineCollegePoint = new Location("");
    skylineCollegePoint.setLatitude(37.629892);
    skylineCollegePoint.setLongitude(-122.467411);
    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(skylineCollegePoint, geofence.features));

    // Pacifica
    Location pacificaPoint = new Location("");
    pacificaPoint.setLatitude(37.638728);
    pacificaPoint.setLongitude(-122.487710);
    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(pacificaPoint, geofence.features));

    // Geneva and Mission
    Location genevaAndMissionPoint = new Location("");
    genevaAndMissionPoint.setLatitude(37.716413);
    genevaAndMissionPoint.setLongitude(-122.441007);
    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(genevaAndMissionPoint, geofence.features));

    // City College of San Francisco
    Location cityCollegePoint = new Location("");
    cityCollegePoint.setLatitude(37.725205);
    cityCollegePoint.setLongitude(-122.452337);
    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(cityCollegePoint, geofence.features));

    // Candlestick Cove
    Location candlestickCovePoint = new Location("");
    candlestickCovePoint.setLatitude(37.709029);
    candlestickCovePoint.setLongitude(-122.393607);
    assertTrue(GeofenceArbiter.checkLocationAgainstFeatures(candlestickCovePoint, geofence.features));

    // A bad point on the 101
    Location route101Point = new Location("");
    route101Point.setLatitude(37.716277);
    route101Point.setLongitude(-122.398499);
    assertFalse(GeofenceArbiter.checkLocationAgainstFeatures(route101Point, geofence.features));
  }
}
