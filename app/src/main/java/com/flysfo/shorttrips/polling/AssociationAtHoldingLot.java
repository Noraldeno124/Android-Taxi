package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.model.driver.DriverResponse;

/**
 * Created by mattluedke on 2/11/16.
 */
public class AssociationAtHoldingLot extends AssociationPoller {
  void successLotCounterManager(DriverResponse.Driver driver) {

  }
  void checkForFallback() {
    FallbackChecks.paymentFallbackCheck();
  }
}
