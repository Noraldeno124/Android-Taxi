package com.flysfo.shorttrips.model.trip;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mattluedke on 2/12/16.
 */
public class TripValidationResponse implements Serializable {
  public TripValidation response;

  public class TripValidation implements Serializable {
    @SerializedName("trip_valid")
    public Boolean valid;

    @SerializedName("validation_steps")
    public ValidationStepWrapper[] validationSteps;

    public class ValidationStepWrapper implements Serializable {
      String description;

      @SerializedName("step_id")
      Integer validationStep;

      @SerializedName("step_name")
      public String name;
    }
  }
}
