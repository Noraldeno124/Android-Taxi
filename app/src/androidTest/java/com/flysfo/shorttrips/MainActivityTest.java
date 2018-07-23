package com.flysfo.shorttrips;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.flight.FlightsTabFragment;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.lot.LotFragment;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.networking.Url;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.statemachine.easyflow.EasyFlow;
import com.flysfo.shorttrips.trip.TripFragment;

/**
 * Created by mattluedke on 11/13/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public MainActivityTest() {
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

  // VIEWS

  public void testViewPager() {
    final ViewPager viewPager = (ViewPager) mainActivity.findViewById(R.id.viewpager_home);
    assertNotNull(viewPager);

    FragmentPagerAdapter adapter = (FragmentPagerAdapter) viewPager.getAdapter();

    assertEquals(3, adapter.getCount());
    assertEquals(FlightsTabFragment.class, adapter.getItem(0).getClass());
    assertEquals(LotFragment.class, adapter.getItem(1).getClass());
    assertEquals(TripFragment.class, adapter.getItem(2).getClass());
  }

  public void testMenu() {
    assertTrue(mainActivity.settingsMenuItemActive());
  }

  public void testInitLocationManager() {
    SfoLocationManager sfoLocationManager = SfoLocationManager.getInstance(mainActivity);
    assertNotNull(sfoLocationManager);
    SfoLocationManager.getInstance(mainActivity).start();
  }

  static public void eventShouldLeadToState(Event event, State state) throws InterruptedException {

    StateManager stateManager = StateManager.getInstance();
    EasyFlow machine = stateManager.getMachine();
    assertNotNull(machine);

    if (stateManager.getState() == state) {
      return;
    }
    stateManager.trigger(event);
    assertTrue(stateManager.getState() == state);
  }
}
