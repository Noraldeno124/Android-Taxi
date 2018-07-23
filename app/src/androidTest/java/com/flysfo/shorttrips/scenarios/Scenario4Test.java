package com.flysfo.shorttrips.scenarios;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.MainActivityTest;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.networking.Url;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Util;

/**
 * Created by mattluedke on 2/2/16.
 */
public class Scenario4Test extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public Scenario4Test() {
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

  public void testScenario4WithLogOutAfterFail() throws InterruptedException {

    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_TAXI_LOOP, State.READY);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_BUFFERED_EXIT, State.WAITING_FOR_EXIT_AVI);

    MainActivityTest.eventShouldLeadToState(Event.EXIT_AVI_CHECK_COMPLETE, State.STARTING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_STARTED, State.IN_PROGRESS);

    TripManager.getInstance().invalidate(ValidationStep.GEOFENCE);
    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_SHORT_TRIP_GEOFENCE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.LOGGED_OUT, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_TAXI_LOOP, State.READY);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_BUFFERED_EXIT, State.WAITING_FOR_EXIT_AVI);

    MainActivityTest.eventShouldLeadToState(Event.EXIT_AVI_CHECK_COMPLETE, State.STARTING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_STARTED, State.IN_PROGRESS);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_BUFFERED_EXIT, State.WAITING_FOR_RE_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_RE_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_RE_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_RE_ENTRY, State.VALIDATING_TRIP);

    TripManager.getInstance().validate();
    assertTrue(StateManager.getInstance().getState() == State.WAITING_IN_HOLDING_LOT);
  }

  public void testScenario4WithLoggedOutBeforeFail() throws InterruptedException {

    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_TAXI_LOOP, State.READY);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_BUFFERED_EXIT, State.WAITING_FOR_EXIT_AVI);

    MainActivityTest.eventShouldLeadToState(Event.EXIT_AVI_CHECK_COMPLETE, State.STARTING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_STARTED, State.IN_PROGRESS);

    TripManager.getInstance().invalidate(ValidationStep.USER_LOGOUT);
    MainActivityTest.eventShouldLeadToState(Event.LOGGED_OUT, State.NOT_READY);
  }

  public void testScenario4WithAppQuitBeforeFail() throws InterruptedException {

    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_TAXI_LOOP, State.READY);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_BUFFERED_EXIT, State.WAITING_FOR_EXIT_AVI);

    MainActivityTest.eventShouldLeadToState(Event.EXIT_AVI_CHECK_COMPLETE, State.STARTING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_STARTED, State.IN_PROGRESS);

    MainActivityTest.eventShouldLeadToState(Event.APP_QUIT, State.NOT_READY);
  }

  public void testScenario4WithAppQuitAfterFail() throws InterruptedException {
    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_TAXI_LOOP, State.READY);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_BUFFERED_EXIT, State.WAITING_FOR_EXIT_AVI);

    MainActivityTest.eventShouldLeadToState(Event.EXIT_AVI_CHECK_COMPLETE, State.STARTING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_STARTED, State.IN_PROGRESS);

    TripManager.getInstance().invalidate(ValidationStep.GEOFENCE);
    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_SHORT_TRIP_GEOFENCE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.APP_QUIT, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_TAXI_LOOP, State.READY);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_BUFFERED_EXIT, State.WAITING_FOR_EXIT_AVI);

    MainActivityTest.eventShouldLeadToState(Event.EXIT_AVI_CHECK_COMPLETE, State.STARTING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_STARTED, State.IN_PROGRESS);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_BUFFERED_EXIT, State.WAITING_FOR_RE_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_RE_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_RE_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_RE_ENTRY, State.VALIDATING_TRIP);

    MainActivityTest.eventShouldLeadToState(Event.TRIP_INVALIDATED, State.NOT_READY);
  }
}
