package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.statemachine.Event;

/**
 * Created by mattluedke on 2/11/16.
 */
public class WaitingForReEntryAvi extends AviPoller {

  Long acceptableTimeDifference() {
    return null;
  }

  GtmsLocation[] expectedAvis() {
    return new GtmsLocation[]{GtmsLocation.TAXI_ENTRY, GtmsLocation.TAXI_STATUS};
  }

  Event successEvent() {
    return Event.LATEST_AVI_AT_RE_ENTRY;
  }

  void checkForFallback() {
    FallbackChecks.reEntryFallbackCheck();
  }

  void callbackLotCounterManager(DriverResponse.Driver driver){}
}
