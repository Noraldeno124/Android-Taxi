package com.flysfo.shorttrips.events;

import com.flysfo.shorttrips.model.trip.ValidationStep;

/**
 * Created by mattluedke on 2/12/16.
 */
public class TripInvalidated {
  public final Integer tripId;
  public final ValidationStep[] validationSteps;

  public TripInvalidated(Integer tripId, ValidationStep[] validationSteps) {
    this.tripId = tripId;
    this.validationSteps = validationSteps;
  }
}
