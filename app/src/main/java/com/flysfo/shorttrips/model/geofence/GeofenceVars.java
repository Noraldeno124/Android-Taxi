package com.flysfo.shorttrips.model.geofence;

import android.content.Context;

import com.flysfo.shorttrips.R;

public class GeofenceVars {

  private static GeofenceVars instance = null;

  public static GeofenceVars getInstance() {
    if (instance == null) {
      throw new NullPointerException("trying to access GeofenceVars before initialized");
    }
    return instance;
  }

  public static GeofenceVars getInstance(Context context) {
    if (instance == null) {
      instance = new GeofenceVars(context);
    }
    return instance;
  }

  private LocalGeofence domesticPickupGeofence;
  private LocalGeofence taxiMergedGeofence;
  private LocalGeofence taxiWaitingZone;
  private LocalGeofence terminalExitBufferedGeofence;

  private GeofenceVars(Context context) {
    this.domesticPickupGeofence = LocalGeofenceFactory.make(context, R.raw.domestic_pax_pickup,
        SfoGeofence.SFO_TAXI_DOMESTIC_EXIT);
    this.taxiMergedGeofence = LocalGeofenceFactory.make(context, R.raw.taxi_sfo_merged,
        SfoGeofence.TAXI_SFO_MERGED);
    this.taxiWaitingZone = LocalGeofenceFactory.make(context, R.raw.taxi_waiting_zone,
        SfoGeofence.TAXI_WAITING_ZONE);
    this.terminalExitBufferedGeofence = LocalGeofenceFactory.make(context, R.raw
        .terminal_exit_buffered, SfoGeofence.TAXI_EXIT_BUFFERED);
  }

  public LocalGeofence getDomesticPickupGeofence() {
    return domesticPickupGeofence;
  }

  public LocalGeofence getTaxiMergedGeofence() {
    return taxiMergedGeofence;
  }

  public LocalGeofence getTaxiWaitingZone() {
    return taxiWaitingZone;
  }

  public LocalGeofence getTerminalExitBufferedGeofence() {
    return terminalExitBufferedGeofence;
  }

  public LocalGeofence[] getAllGeofences() {
    return new LocalGeofence[] {
        domesticPickupGeofence,
        taxiMergedGeofence,
        taxiWaitingZone,
        terminalExitBufferedGeofence
    };
  }
}
