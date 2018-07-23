package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.statemachine.Event;

/**
 * Created by mattluedke on 2/8/16.
 */
public class WaitingForEntryCid extends CidPoller {

  GtmsLocation[] expectedCids() {
    return new GtmsLocation[] {GtmsLocation.TAXI_ENTRY};
  }

  Event successEvent() {
    return Event.LATEST_CID_IS_ENTRY_CID;
  }

  long acceptableCidAge() {
    return Long.MAX_VALUE;
  }

  void checkForFallback() {
    FallbackChecks.entryFallbackCheck();
  }
}
