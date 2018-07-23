package com.flysfo.shorttrips.handlers;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.trip.MobileState;
import com.flysfo.shorttrips.networking.RetryManager;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.trip.TripManager;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 3/3/16.
 */
public class LoggedOut implements ContextHandler {

  private Handler timerHandler = new Handler();

  @Override
  public void call(StatefulContext context) throws Exception {

    Location location = SfoLocationManager.getInstance().getLastKnownLocation();
    DriverResponse.Driver currentDriver =  DriverManager.getInstance().getCurrentDriver();

    if (location != null
        && currentDriver != null
        && currentDriver.sessionId != null) {

      updateLoggedOut(location, currentDriver.sessionId, 0);

    } else {
      Answers.getInstance().logCustom(new CustomEvent("ready call, invalid location and/or " +
        "driver"));
      Log.e("error", "invalid location and/or driver");
    }
  }

  private void updateLoggedOut(
    final Location location,
    final Integer sessionId,
    final int retryCount) {

    if (MobileState.lastKnown == MobileState.LOGGED_OUT) {
      return;
    }

    SfoApi.getInstance().updateMobileState(
        MobileState.LOGGED_OUT.getValue(),
        location.getLatitude(),
        location.getLongitude(),
        sessionId,
        TripManager.getInstance().getTripId()
    ).enqueue(new Callback<Void>() {
      @Override
      public void onResponse(Call<Void> call,  Response<Void> response) {
        EventBus.getDefault().post(new NetworkResponse(response));
        if (response.isSuccessful()) {
          MobileState.lastKnown = MobileState.LOGGED_OUT;
        }
      }

      @Override
      public void onFailure(Call<Void> call, Throwable t) {
        EventBus.getDefault().post(new NetworkResponse(t));

        if (retryCount < RetryManager.MAX_RETRIES) {
          timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              updateLoggedOut(location, sessionId, retryCount + 1);
            }
          }, RetryManager.timeInterval(retryCount));
        }
      }
    });
  }
}
