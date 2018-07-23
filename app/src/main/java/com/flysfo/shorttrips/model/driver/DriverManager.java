package com.flysfo.shorttrips.model.driver;

import android.content.Context;
import android.os.Handler;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.events.SessionReloaded;
import com.flysfo.shorttrips.model.driver.DriverResponse.Driver;
import com.flysfo.shorttrips.model.lotcounter.TransactionLog;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.networking.RetryManager;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.notification.NotificationManager;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverManager {

  private Context context;
  private Driver currentDriver;
  private Vehicle currentVehicle;
  private TransactionLog currentTransactionLog;
  private Long sessionCreationDate;
  private static final long VALID_SESSION_AGE = 24 * 60 * 60 * 1000; // 24hrs
  private static final String AUTH_FAIL_LOG = "android: failed to authenticate many times";

  private Handler timerHandler = new Handler();

  private static DriverManager instance = null;
  private DriverManager(Context context) {
    this.context = context;
  }
  public static DriverManager getInstance() {
    if (instance == null) {
      throw new NullPointerException("trying to access DriverManager before initialized");
    }
    return instance;
  }

  public static DriverManager getInstance(Context context) {
    if (instance == null) {
      instance = new DriverManager(context);
    } else {
      instance.context = context;
    }
    return instance;
  }

  public Driver getCurrentDriver() {
    return currentDriver;
  }

  public Vehicle getCurrentVehicle() {
    return currentVehicle;
  }

  public TransactionLog getCurrentTransactionLog() {
    return currentTransactionLog;
  }

  public void setCurrentDriver(Driver driver) {
    currentDriver = driver;

    if (currentDriver != null) {
      sessionCreationDate = System.currentTimeMillis();
    } else {
      sessionCreationDate = null;
    }
  }

  private boolean hasValidSession() {
    return sessionCreationDate != null
        && sessionCreationDate > System.currentTimeMillis() - VALID_SESSION_AGE;
  }

  public void setCurrentVehicle(Vehicle vehicle) {
    currentVehicle = vehicle;

    if (currentDriver != null && currentVehicle != null) {
      StateManager.getInstance().trigger(Event.DRIVER_AND_VEHICLE_ASSOCIATED);
    }
  }

  public void setCurrentTransactionLog(TransactionLog transactionLog) {
    currentTransactionLog = transactionLog;
  }

  public void callWithValidSession(ValidDriverRunnable runnable) {
    callWithValidSession(0, runnable);
  }

  private void callWithValidSession(final int retryCount, final ValidDriverRunnable runnable) {
    if (hasValidSession()) {
      runnable.run(currentDriver);
    } else {

      EventBus.getDefault().post(new SessionReloaded());

      final DriverCredential credential = DriverCredential.load(context);

      if (credential == null) {
        TripManager.getInstance().invalidate(ValidationStep.USER_LOGOUT);
        Answers.getInstance().logCustom(new CustomEvent("no credentials"));
        return;
      }

      SfoApi.getInstance()
          .authenticateDriver(credential.username, credential.password, credential.latitude,
              credential.longitude, credential.deviceUuid, credential.osVersion, credential.deviceOs)
          .enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
              EventBus.getDefault().post(new NetworkResponse(response));

              Driver driver = response.body().response;

              if (driver != null) {
                credential.save(context);
                DriverManager.getInstance().setCurrentDriver(driver);
                NotificationManager.refreshAll(context);
                runnable.run(driver);

              } else if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    callWithValidSession(retryCount + 1, runnable);
                  }
                }, RetryManager.timeInterval(retryCount));

              } else {
                Answers.getInstance().logCustom(new CustomEvent(AUTH_FAIL_LOG));
              }
            }

            @Override
            public void onFailure(Call<DriverResponse> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));

              if (retryCount < RetryManager.MAX_RETRIES) {
                timerHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    callWithValidSession(retryCount + 1, runnable);
                  }
                }, RetryManager.timeInterval(retryCount));

              } else {
                Answers.getInstance().logCustom(new CustomEvent(AUTH_FAIL_LOG));
              }
            }
          });
    }
  }
}
