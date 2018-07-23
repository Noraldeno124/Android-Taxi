package com.flysfo.shorttrips.model.trip;

/**
 * Created by pierreexygy on 2/29/16.
 */
public enum MobileState {
  READY(1),
  NOT_READY(2),
  IN_PROGRESS(4),
  VALID(5),
  INVALID(6),
  NO_SERVICE(7),
  CRASHED(8),
  LOGGED_OUT(9);

  public static MobileState lastKnown;

  private final int state;

  MobileState(int state) {
    this.state = state;
  }

  public int getValue() {
    return state;
  }

  public String toName() {
    switch (this) {
      case READY:
        return "mobile_ready";
      case NOT_READY:
        return "mobile_not_ready";
      case IN_PROGRESS:
        return "trip_in_progress";
      case VALID:
        return "trip_end_valid";
      case INVALID:
        return "trip_end_invalid";
      case NO_SERVICE:
        return "mobile_no_service";
      case CRASHED:
        return "mobile_crashed";
      case LOGGED_OUT:
        return "user_logged_out";
      default:
        return "";
    }
  }
}
