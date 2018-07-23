package com.flysfo.shorttrips.trip;

import com.flysfo.shorttrips.R;

/**
 * Created by mattluedke on 3/2/16.
 */
public enum StatePrompt {
  TURN_ON_GPS,
  GO_TO_SFO,
  PAY,
  READY,
  IN_PROGRESS;

  int visualStringRes() {
    switch (this) {
      case TURN_ON_GPS:
        return R.string.location_services_required;
      case GO_TO_SFO:
        return R.string.sfo_garage_entry_required_prior_to_next_trip;
      case PAY:
        return R.string.waiting_for_dispatch;
      case READY:
        return R.string.trip_pending_until_exit_from_sfo;
      case IN_PROGRESS:
        return R.string.trip_in_progress;
      default:
        throw new RuntimeException("invalid enum");
    }
  }

  int audioStringRes() {
    switch (this) {
      case TURN_ON_GPS:
        return R.string.location_services_must_be_turned_on_in_order_to_monitor_your_trip;
      case GO_TO_SFO:
        return R.string.sfo_garage_entry_required_prior_to_next_trip;
      case PAY:
        return R.string.waiting_for_dispatch;
      case READY:
        return R.string.the_trip_will_start_when_the_vehicle_exits_sfo;
      case IN_PROGRESS:
        return R.string.the_trip_has_started_and_will_be_monitored;
      default:
        throw new RuntimeException("invalid enum");
    }
  }

  int imageRes() {
    switch (this) {
      case TURN_ON_GPS:
      case GO_TO_SFO:
        return R.drawable.map;
      case PAY:
        return R.drawable.hourglass;
      case READY:
        return R.drawable.pin;
      case IN_PROGRESS:
        return R.drawable.car;
      default:
        throw new RuntimeException("invalid enum");
    }
  }
}