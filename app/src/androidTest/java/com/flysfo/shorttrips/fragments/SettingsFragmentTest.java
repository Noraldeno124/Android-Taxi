package com.flysfo.shorttrips.fragments;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.networking.Url;
import com.flysfo.shorttrips.settings.SettingsFragment;

/**
 * Created by mattluedke on 2/2/16.
 */
public class SettingsFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public SettingsFragmentTest() {
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

  public void testSettings() {

    SettingsFragment settingsFragment = new SettingsFragment();
    mainActivity.getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, settingsFragment)
        .addToBackStack(null)
        .commit();

    mainActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mainActivity.getFragmentManager().executePendingTransactions();
      }
    });

    getInstrumentation().waitForIdleSync();

    assertNotNull(settingsFragment);
    assertTrue(settingsFragment.isVisible());
    assertTrue(settingsFragment.isResumed());
    assertNotNull(settingsFragment.findPreference(mainActivity.getString(R.string.audio_setting_title)));
    assertNotNull(settingsFragment.findPreference(mainActivity.getString(R.string.logout)));
    assertNull(settingsFragment.findPreference("hahaha"));
  }
}
