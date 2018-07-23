package com.flysfo.shorttrips.avi;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.networking.Url;
import com.flysfo.shorttrips.util.Util;

/**
 * Created by mattluedke on 10/26/16.
 */

public class AviManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public AviManagerTest() {
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

  public void testAviManager() {
    AviManager.getInstance().setLatestAviLocation(GtmsLocation.TAXI_ENTRY);
    assertTrue(AviManager.getInstance().latestAviInTaxiEntryOrStatus());

    AviManager.getInstance().setLatestAviLocation(GtmsLocation.DOM_EXIT);
    assertFalse(AviManager.getInstance().latestAviInTaxiEntryOrStatus());
  }
}
