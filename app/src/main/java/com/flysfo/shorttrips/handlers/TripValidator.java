package com.flysfo.shorttrips.handlers;

import android.os.Build;
import android.os.Handler;

import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.driver.Vehicle;
import com.flysfo.shorttrips.model.trip.TripValidationResponse;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.networking.RetryManager;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.trip.TripDateTransform;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripValidator implements ContextHandler {

  private Handler timerHandler = new Handler();

  @Override
  public void call(StatefulContext context) {
    DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
      @Override
      public void run(DriverResponse.Driver driver) {
        if (!Util.TESTING) {
          validateTrip(0);
        }
      }
    });
  }

  private void validateTrip(final int retryCount) {

    final Vehicle vehicle = DriverManager.getInstance().getCurrentVehicle();
    final Integer tripId = TripManager.getInstance().getTripId();

    if (vehicle != null) {

      Date deviceTimestamp = TripManager.getInstance().getStartTime();
      if (deviceTimestamp == null) {
        deviceTimestamp = new Date();
      }
      final Date finalDeviceTimestamp = deviceTimestamp;

      DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
        @Override
        public void run(DriverResponse.Driver driver) {
          Call<TripValidationResponse> call = SfoApi.getInstance().endTrip(
            tripId,
            TripDateTransform.fromDate(finalDeviceTimestamp),
            Build.SERIAL,
            vehicle.medallion,
            driver.sessionId,
            driver.cardId,
            vehicle.vehicleId);
          call.clone().enqueue(new Callback<TripValidationResponse>() {
            @Override
            public void onResponse(Call<TripValidationResponse> call, Response<TripValidationResponse>
              validationResponse) {
              EventBus.getDefault().post(new NetworkResponse(validationResponse));

              if (validationResponse.body() != null
                && validationResponse.body().response != null) {

                TripValidationResponse.TripValidation validation = validationResponse.body().response;

                if (validation.valid) {
                  TripManager.getInstance().validate();
                } else {
                  TripManager.getInstance().invalidated(ValidationStep.from(validation.validationSteps));
                }

              } else if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    validateTrip(retryCount + 1);
                  }
                }, RetryManager.timeInterval(retryCount));

              } else {
                TripManager.getInstance().invalidated(
                  new ValidationStep[] { ValidationStep.NETWORK_FAILURE }
                );
              }
            }

            @Override
            public void onFailure(Call<TripValidationResponse> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));

              if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    validateTrip(retryCount + 1);
                  }
                }, RetryManager.timeInterval(retryCount));

              } else {
                TripManager.getInstance().invalidated(
                  new ValidationStep[] { ValidationStep.NETWORK_FAILURE }
                );
              }
            }
          });
        }
      });
    } else {
      throw new RuntimeException("invalid vehicle");
    }
  }
}
