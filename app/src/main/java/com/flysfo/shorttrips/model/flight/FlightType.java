package com.flysfo.shorttrips.model.flight;

import com.flysfo.shorttrips.R;

import java.lang.reflect.Array;

/**
 * Created by mattluedke on 3/7/16.
 */
public enum FlightType {
  ARRIVALS,
  DEPARTURES;

  public static FlightType[] flightTypes() {
    return new FlightType[]{ARRIVALS, DEPARTURES};
  }

  public int titleStringRes() {
    switch (this) {
      case ARRIVALS:
        return R.string.flight_status_arrivals;
      case DEPARTURES:
        return R.string.flight_status_departures;
      default:
        throw new RuntimeException("bad enum");
    }
  }


}
