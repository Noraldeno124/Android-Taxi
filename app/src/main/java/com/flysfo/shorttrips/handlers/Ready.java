package com.flysfo.shorttrips.handlers;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse.Driver;
import com.flysfo.shorttrips.model.trip.MobileState;
import com.flysfo.shorttrips.networking.RetryManager;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ready implements ContextHandler {

  private Handler timerHandler = new Handler();

  @Override
  public void call(StatefulContext context) throws Exception {
    updateMobileState(0);
  }

  private void updateMobileState(final int retryCount) {

    if (MobileState.lastKnown == MobileState.READY) {
      return;
    }

    final Location location = SfoLocationManager.getInstance().getLastKnownLocation();

    if (location != null) {

      DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
        @Override
        public void run(Driver driver) {
          SfoApi.getInstance().updateMobileState(
            MobileState.READY.getValue(),
            location.getLatitude(),
            location.getLongitude(),
            driver.sessionId,
            null
          ).clone().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
              EventBus.getDefault().post(new NetworkResponse(response));
              if (response.isSuccessful()) {
                MobileState.lastKnown = MobileState.READY;

              } else if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    updateMobileState(retryCount + 1);
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
                    updateMobileState(retryCount + 1);
                  }
                }, RetryManager.timeInterval(retryCount));
              }
            }
          });
        }
      });
    } else {
      Answers.getInstance().logCustom(new CustomEvent("ready call, invalid location"));
      Log.e("error", "invalid location");
    }
  }
}
