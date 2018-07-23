package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.lotcounter.LotCounterManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.statemachine.Event;

/**
 * Created by mattluedke on 2/11/16.
 */
public class WaitingForTaxiLoopAvi extends AviPoller {

  Long acceptableTimeDifference() {
    return 1000l*15l*60l;
  }

  GtmsLocation[] expectedAvis() {
    return new GtmsLocation[] {GtmsLocation.DTA_RECIRCULATION,
        GtmsLocation.TAXI_MAIN_LOT};
  }

  Event successEvent() {
    return Event.LATEST_AVI_AT_TAXI_LOOP;
  }

  void callbackLotCounterManager(DriverResponse.Driver driver){
    LotCounterManager.getInstance().existsGarageThroughCurbside(driver);
  }

  void checkForFallback() {
    FallbackChecks.paymentFallbackCheck();
  }
}
