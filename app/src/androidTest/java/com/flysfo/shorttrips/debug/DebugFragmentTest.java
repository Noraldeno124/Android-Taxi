package com.flysfo.shorttrips.debug;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.networking.Url;

/**
 * Created by mattluedke on 2/9/16.
 */
public class DebugFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public DebugFragmentTest() {
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

  public void testDebug() {

    DebugFragment debugFragment = new DebugFragment();
    mainActivity.getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, debugFragment)
        .addToBackStack(null)
        .commit();

    mainActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mainActivity.getFragmentManager().executePendingTransactions();
      }
    });

    getInstrumentation().waitForIdleSync();

    assertNotNull(debugFragment);
    assertTrue(debugFragment.isVisible());
    assertTrue(debugFragment.isResumed());
    assertNotNull(debugFragment.debugView);
  }
}
