package com.flysfo.shorttrips.model.trip;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.trip.TripValidationResponse.TripValidation.ValidationStepWrapper;

/**
 * Created by mattluedke on 2/16/16.
 */
public enum ValidationStep {
  UNSPECIFIED,
  DURATION,
  VEHICLE,
  DRIVER_CARD_ID,
  MAC_ADDRESS,
  GEOFENCE,
  GPS_FAILURE,
  NETWORK_FAILURE,
  USER_LOGOUT,
  APP_QUIT,
  APP_CRASH;

  public static ValidationStep fromInt(Integer integer) {
    switch (integer) {

      case 1:
        return DURATION;
      case 2:
        return VEHICLE;
      case 3:
        return DRIVER_CARD_ID;
      case 4:
        return MAC_ADDRESS;
      case 5:
        return GEOFENCE;
      case 6:
        return GPS_FAILURE;
      case 7:
        return NETWORK_FAILURE;
      case 8:
        return USER_LOGOUT;
      case 9:
        return APP_QUIT;
      case 10:
        return APP_CRASH;

      default:
        return UNSPECIFIED;
    }
  }

  public Integer toInt() {
    switch (this) {
      case DURATION:
        return 1;
      case VEHICLE:
        return 2;
      case DRIVER_CARD_ID:
        return 3;
      case MAC_ADDRESS:
        return 4;
      case GEOFENCE:
        return 5;
      case GPS_FAILURE:
        return 6;
      case NETWORK_FAILURE:
        return 7;
      case USER_LOGOUT:
        return 8;
      case APP_QUIT:
        return 9;
      case APP_CRASH:
        return 10;
      default:
        return 0;
    }
  }

  public int visualStringRes() {
    switch (this) {
      case DURATION:
        return R.string.long_trip_duration_exceeded_two_hours;
      case VEHICLE:
        return R.string.long_trip_vehicle_mismatch;
      case DRIVER_CARD_ID:
        return R.string.long_trip_card_error;
      case MAC_ADDRESS:
        return R.string.long_trip_phone_error;
      case GEOFENCE:
        return R.string.long_trip_outside_geofence;
      case GPS_FAILURE:
        return R.string.long_trip_location_services_unavailable;
      case NETWORK_FAILURE:
        return R.string.long_trip_network_failure;
      case USER_LOGOUT:
        return R.string.long_trip_user_logged_out;
      case APP_QUIT:
        return R.string.long_trip_app_quit;
      case APP_CRASH:
        return R.string.long_trip_app_crash;
      case UNSPECIFIED:
        return R.string.long_trip_unknown;
      default:
        throw new RuntimeException("Bad Enum");
    }
  }

  public int audioStringRes() {
    switch (this) {
      case DURATION:
        return R.string.long_trip_the_duration_exceeded_two_hours;
      case VEHICLE:
        return R.string.long_trip_the_vehicle_at_the_start_was_not_the_same_at_the_end_of_the_trip;
      case DRIVER_CARD_ID:
        return R.string.long_trip_the_card_tapped_at_the_start_was_not_identified_at_the_end_of_the_trip;
      case MAC_ADDRESS:
        return R.string.long_trip_the_phone_at_the_start_was_not_identified_at_the_end_of_the_trip;
      case GEOFENCE:
        return R.string.long_trip_the_vehicle_was_located_outside_the_geofence_while_trip_was_in_progress;
      case GPS_FAILURE:
        return R.string.long_trip_location_based_services_were_unavailable_while_the_trip_was_in_progress;
      case USER_LOGOUT:
        return R.string.long_trip_logout_occurred_while_the_trip_was_in_progress;
      case UNSPECIFIED:
        return R.string.long_trip_unknown;
      default:
        throw new RuntimeException("Bad Enum");
    }
  }

  public static ValidationStep[] from(ValidationStepWrapper[] wrappers) {
    ValidationStep[] steps = new ValidationStep[wrappers.length];

    for (int i = 0; i < wrappers.length; i++) {
      steps[i] = ValidationStep.fromInt(wrappers[i].validationStep);
    }

    return steps;
  }
}
