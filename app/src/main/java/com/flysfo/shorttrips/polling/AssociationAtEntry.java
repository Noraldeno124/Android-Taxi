package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.lotcounter.LotCounterManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;

/**
 * Created by mattluedke on 2/9/16.
 */
public class AssociationAtEntry extends AssociationPoller {
  void successLotCounterManager(DriverResponse.Driver driver) {
    LotCounterManager.getInstance().reentersGarage(driver, "SHORT");
  }

  void checkForFallback() {
    FallbackChecks.entryFallbackCheck();
  }
}
