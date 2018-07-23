package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.avi.AviManager;
import com.flysfo.shorttrips.geofence.GeofenceManager;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;

/**
 * Created by mattluedke on 2/11/16.
 */
public class FallbackChecks {

  static void entryFallbackCheck() {
    if (!GeofenceManager.getInstance().stillInsideTaxiWaitZone()) {
      StateManager.getInstance().trigger(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK);
    }
  }

  static void paymentFallbackCheck() {
    if (!GeofenceManager.getInstance().stillInDomesticExitNotInHoldingLot()) {
      if (AviManager.getInstance().latestAviInTaxiEntryOrStatus()) {
        StateManager.getInstance().trigger(Event.NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS);
      } else {
        StateManager.getInstance().trigger(Event.NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_NOT_ENTRY_OR_STATUS);
      }
    }
  }

  static void reEntryFallbackCheck() {
    if (!GeofenceManager.getInstance().stillInsideSfoBufferedExit()) {
      StateManager.getInstance().trigger(Event.NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK);
    }
  }
}
