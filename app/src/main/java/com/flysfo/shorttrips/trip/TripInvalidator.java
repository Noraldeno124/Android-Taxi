package com.flysfo.shorttrips.trip;

import android.os.Handler;

import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.networking.RetryManager;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.ping.PingManager;
import com.flysfo.shorttrips.service.InvalidateCallback;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class TripInvalidator {

  private static Handler timerHandler = new Handler();

  static void makeInvalidateCall(
    final Integer tripId,
    final String deviceTimestamp,
    final Integer validationStep,
    final InvalidateCallback callback
  ) {
    makeInvalidateCall(0, tripId, deviceTimestamp, validationStep, callback);
  }

  private static void makeInvalidateCall(
    final int retryCount,
    final Integer tripId,
    final String deviceTimestamp,
    final Integer validationStep,
    final InvalidateCallback callback
  ) {

    PingManager.getInstance().sendOldPings(tripId);

    DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
      @Override
      public void run(DriverResponse.Driver driver) {
        SfoApi.getInstance().invalidateTrip(
          tripId,
          driver.sessionId,
          validationStep,
          deviceTimestamp
        ).clone().enqueue(new Callback<Void>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response) {
            EventBus.getDefault().post(new NetworkResponse(response));

            if (response.isSuccessful()) {
              if (callback != null) {
                callback.invalidated();
              }
            } else if (retryCount < RetryManager.MAX_RETRIES) {
              timerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  makeInvalidateCall(
                    retryCount + 1,
                    tripId,
                    deviceTimestamp,
                    validationStep,
                    callback);
                }
              }, RetryManager.timeInterval(retryCount));
            }
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t) {
            EventBus.getDefault().post(new NetworkResponse(t));

            if (retryCount < RetryManager.MAX_RETRIES) {
              timerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  makeInvalidateCall(
                    retryCount + 1,
                    tripId,
                    deviceTimestamp,
                    validationStep,
                    callback);
                }
              }, RetryManager.timeInterval(retryCount));
            }
          }
        });
      }
    });
  }
}
