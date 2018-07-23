package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.statemachine.Event;

/**
 * Created by mattluedke on 2/11/16.
 */
public class WaitingForPaymentCid extends CidPoller {

  GtmsLocation[] expectedCids() {
    return new GtmsLocation[] {GtmsLocation.TAXI_MAIN_LOT};
  }

  Event successEvent() {
    return Event.LATEST_CID_IS_PAYMENT_CID;
  }

  long acceptableCidAge() {
    return 4 * 60 * 60 * 1000;
  }

  void checkForFallback() {
    FallbackChecks.paymentFallbackCheck();
  }
}