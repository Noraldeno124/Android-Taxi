package com.flysfo.shorttrips.auth;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.networking.Url;

/**
 * Created by mattluedke on 10/26/16.
 */

public class SplashActivityTest extends ActivityInstrumentationTestCase2<SplashActivity> {

  SplashActivity mSplashActivity;

  public SplashActivityTest() {
    super(SplashActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!Url.isStaging()) {
      throw new RuntimeException("can't use production URL in tests");
    }

    mSplashActivity = getActivity();
  }

  public void testSplashActivity() {
    assertTrue(mSplashActivity != null);
  }
}
