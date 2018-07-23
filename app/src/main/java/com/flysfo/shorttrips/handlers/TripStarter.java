package com.flysfo.shorttrips.handlers;

import android.os.Build;
import android.os.Handler;

import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.lotcounter.LotCounterManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.driver.Vehicle;
import com.flysfo.shorttrips.model.trip.TripIdResponse;
import com.flysfo.shorttrips.networking.RetryManager;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
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


public class TripStarter implements ContextHandler {

  private Handler timerHandler = new Handler();

  @Override
  public void call(StatefulContext context) throws Exception {
    if (!Util.TESTING) {
      startTrip(0);
    }
  }

  private void startTrip(final int retryCount) {

    final Vehicle vehicle = DriverManager.getInstance().getCurrentVehicle();

    if (vehicle != null) {

      Date deviceTimestamp = TripManager.getInstance().getStartTime();
      if (deviceTimestamp == null) {
        deviceTimestamp = new Date();
      }
      final Date finalDeviceTimestamp = deviceTimestamp;

      DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
        @Override
        public void run(DriverResponse.Driver driver) {
          LotCounterManager.getInstance().exitsAirport(driver);

          Call<TripIdResponse> call = SfoApi.getInstance().startTrip(
            TripDateTransform.fromDate(finalDeviceTimestamp),
            Build.SERIAL,
            vehicle.medallion,
            driver.sessionId,
            driver.cardId,
            vehicle.vehicleId
          );
          call.clone().enqueue(new Callback<TripIdResponse>() {
            @Override
            public void onResponse(Call<TripIdResponse> call, Response<TripIdResponse> tripIdResponse) {
              EventBus.getDefault().post(new NetworkResponse(tripIdResponse));

              if (tripIdResponse.body() != null
                && tripIdResponse.body().response != null) {

                TripManager.getInstance().start(tripIdResponse.body().response.tripId);
              } else if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    startTrip(retryCount + 1);
                  }
                }, RetryManager.timeInterval(retryCount));
              } else {
                StateManager.getInstance().trigger(Event.FAILURE);
              }
            }

            @Override
            public void onFailure(Call<TripIdResponse> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));
              if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    startTrip(retryCount + 1);
                  }
                }, RetryManager.timeInterval(retryCount));
              } else {
                StateManager.getInstance().trigger(Event.FAILURE);
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
