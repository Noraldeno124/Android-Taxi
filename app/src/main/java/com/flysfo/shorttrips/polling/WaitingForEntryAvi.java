package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.statemachine.Event;

/**
 * Created by mattluedke on 2/9/16.
 */
public class WaitingForEntryAvi extends AviPoller {

  Long acceptableTimeDifference() {
    return null;
  }

  GtmsLocation[] expectedAvis() {
    return new GtmsLocation[]{GtmsLocation.TAXI_ENTRY};
  }

  Event successEvent() {
    return Event.LATEST_AVI_AT_ENTRY;
  }

  void checkForFallback() {
    FallbackChecks.entryFallbackCheck();
  }

  void callbackLotCounterManager(DriverResponse.Driver driver){}
}
