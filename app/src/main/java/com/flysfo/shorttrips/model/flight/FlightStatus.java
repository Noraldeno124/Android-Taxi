package com.flysfo.shorttrips.model.flight;

import com.flysfo.shorttrips.R;

/**
 * Created by pierreexygy on 3/16/16.
 */
public enum FlightStatus {
  DELAYED,
  ONTIME,
  LANDING,
  LANDED;

  public int toStringRes() {
    switch (this) {
      case DELAYED:
        return R.string.flight_status_delayed;
      case ONTIME:
        return R.string.flight_status_ontime;
      case LANDING:
        return R.string.flight_status_landing;
      case LANDED:
        return R.string.flight_status_landed;
      default:
        throw new RuntimeException("bad enum");
    }
  }

  public int toNumberColor() {
    switch (this) {
      case DELAYED:
        return R.color.flight_status_delayed_text_red;
      default:
        return R.color.flight_status_ontime_text_blue;
    }
  }

  public int toCircleAsset() {
    switch (this) {
      case DELAYED:
        return R.drawable.red_circle;
      default:
        return R.drawable.blue_circle;
    }
  }

  public static FlightStatus fromString(String flightStatus){
    switch (flightStatus){
      case "Delayed":
        return FlightStatus.DELAYED;
      case "On Time":
        return FlightStatus.ONTIME;
      case "Landing":
        return FlightStatus.LANDING;
      case "Landed":
        return FlightStatus.LANDED;
      default:
        return null;
    }
  }

}
