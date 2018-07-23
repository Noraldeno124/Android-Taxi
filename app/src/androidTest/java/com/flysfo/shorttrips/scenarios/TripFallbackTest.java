package com.flysfo.shorttrips.scenarios;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.MainActivityTest;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.networking.Url;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.util.Util;

/**
 * Created by mattluedke on 2/2/16.
 */
public class TripFallbackTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public TripFallbackTest() {
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

  public void testFallbackFromEntry() throws InterruptedException {
    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.OUTSIDE_TAXI_WAIT_ZONE, State.NOT_READY);
  }

  public void testFallbackFromPaymentToNotReady() throws InterruptedException {
    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_NOT_ENTRY_OR_STATUS, State.NOT_READY);
  }

  public void testFallbackFromPaymentToHoldingLot() throws InterruptedException {
    MainActivityTest.eventShouldLeadToState(Event.FAILURE, State.NOT_READY);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_WAITING_ZONE, State.WAITING_FOR_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_AVI_AT_ENTRY, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS, State.WAITING_IN_HOLDING_LOT);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_TAXI_LOOP_EXIT, State.WAITING_FOR_PAYMENT_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_PAYMENT_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_TAXI_LOOP_AVI);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS, State.WAITING_IN_HOLDING_LOT);
  }

  public void testFallbackFromReEntryToInProgress() throws InterruptedException {
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

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_BUFFERED_EXIT, State.WAITING_FOR_RE_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK, State.IN_PROGRESS);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_BUFFERED_EXIT, State.WAITING_FOR_RE_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_RE_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK, State.IN_PROGRESS);

    MainActivityTest.eventShouldLeadToState(Event.INSIDE_BUFFERED_EXIT, State.WAITING_FOR_RE_ENTRY_CID);

    MainActivityTest.eventShouldLeadToState(Event.LATEST_CID_IS_RE_ENTRY_CID, State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY);

    MainActivityTest.eventShouldLeadToState(Event.DRIVER_AND_VEHICLE_ASSOCIATED, State.WAITING_FOR_RE_ENTRY_AVI);

    MainActivityTest.eventShouldLeadToState(Event.NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK, State.IN_PROGRESS);
  }
}
