package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.lotcounter.LotCounterManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;

/**
 * Created by mattluedke on 2/11/16.
 */
public class AssociationAtReEntry extends AssociationPoller {

  void successLotCounterManager(DriverResponse.Driver driver) {
    LotCounterManager.getInstance().entersGarage(driver);
  }

  void checkForFallback() {
    FallbackChecks.reEntryFallbackCheck();
  }


}
