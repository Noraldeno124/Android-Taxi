package com.flysfo.shorttrips.avi;

import com.flysfo.shorttrips.model.gtms.GtmsLocation;

/**
 * Created by mattluedke on 2/11/16.
 */
public class AviManager {

  private static AviManager instance = null;
  private AviManager() {}
  public static AviManager getInstance() {
    if (instance == null) {
      instance = new AviManager();
    }
    return instance;
  }

  private GtmsLocation latestAviLocation;

  public void setLatestAviLocation(GtmsLocation latestAviLocation) {
    this.latestAviLocation = latestAviLocation;
  }

  public Boolean latestAviInTaxiEntryOrStatus() {
    return latestAviLocation != null
        && (latestAviLocation == GtmsLocation.TAXI_ENTRY
          || latestAviLocation == GtmsLocation.TAXI_STATUS);
  }
}
