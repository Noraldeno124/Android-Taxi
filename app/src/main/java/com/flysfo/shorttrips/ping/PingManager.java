package com.flysfo.shorttrips.ping;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.events.PingCreated;
import com.flysfo.shorttrips.events.PingInvalid;
import com.flysfo.shorttrips.events.PingValid;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse.Driver;
import com.flysfo.shorttrips.model.driver.Vehicle;
import com.flysfo.shorttrips.model.ping.Ping;
import com.flysfo.shorttrips.model.ping.PingBatch;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PingManager {

  private Context context;
  private AlarmManager alarmManager;
  private PendingIntent pendingIntent;

  private static PingManager instance = null;

  private PingManager(Context context) {
    this.context = context;
    this.alarmManager = (AlarmManager) context.getSystemService(Context
      .ALARM_SERVICE);
  }

  public static PingManager getInstance(Context context) {
    if (instance == null) {
      instance = new PingManager(context);
    } else {
      instance.context = context;
    }
    return instance;
  }

  public static PingManager getInstance() {
    if (instance == null) {
      throw new NullPointerException("trying to access PingManager before initialized");
    }
    return instance;
  }

  private static final long UPDATE_FREQUENCY = 30 * 1000; // 30 sec

  private Integer invalidPings = 0;
  private static final Integer MAX_INVALID_PINGS = 2;
  private List<Ping> missedPings = new ArrayList<>();

  @Subscribe
  public void pingCreated(PingCreated event) {

    Ping ping = event.ping;

    if (ping.getGeofenceStatus().toBool()) {
      EventBus.getDefault().post(new PingValid(ping));
      invalidPings = 0;

    } else {
      EventBus.getDefault().post(new PingInvalid(ping));
      invalidPings += 1;

      if (invalidPings > MAX_INVALID_PINGS) {
        StateManager.getInstance().trigger(Event.OUTSIDE_SHORT_TRIP_GEOFENCE);
        TripManager.getInstance().invalidate(ValidationStep.GEOFENCE);
        invalidPings = 0;
      }
    }
  }

  public void start() {

    EventBus bus = EventBus.getDefault();
    if (!bus.isRegistered(this)) {
      // TODO: this was causing rare crash; see why lifecycle got out-of-sync
      EventBus.getDefault().register(this);
    }

    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    createPendingIntent();

    scheduleAlarm();
  }

  private void createPendingIntent() {
    Intent notificationIntent = new Intent(context, PingReceiver.class);
    pendingIntent = PendingIntent.getBroadcast(
      context,
      1983,
      notificationIntent,
      PendingIntent.FLAG_UPDATE_CURRENT);
  }

  void scheduleAlarm() {

    // TODO: this is to fix odd crash that pendingIntent is null here...
    if (pendingIntent == null) {
      createPendingIntent();
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis() + UPDATE_FREQUENCY,
        null), pendingIntent);
    } else {
      alarmManager.setExact(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + UPDATE_FREQUENCY,
        pendingIntent
      );
    }
  }

  public void stop() {
    EventBus.getDefault().unregister(this);

    if (alarmManager != null && pendingIntent != null) {
      alarmManager.cancel(pendingIntent);
    } else if (alarmManager != null) {
      Answers.getInstance().logCustom(new CustomEvent("trying to stop pings, null pendingIntent"));
      createPendingIntent();
      alarmManager.cancel(pendingIntent);
    } else if (pendingIntent != null) {
      Answers.getInstance().logCustom(new CustomEvent("trying to stop pings, null alarmManager"));
    } else {
      Answers.getInstance().logCustom(new CustomEvent("trying to stop pings, both alarmManager " +
        "and pendingIntent null"));
    }
  }

  void sendNewPing() {

    final SfoLocationManager manager;

    try {
      manager = SfoLocationManager.getInstance();
    } catch (NullPointerException e) {
      TripManager.getInstance().invalidate(ValidationStep.APP_QUIT);
      return;
    }

    if (manager == null) {
      TripManager.getInstance().invalidate(ValidationStep.APP_QUIT);
      return;
    }

    if (!SfoPreferences.containsCredentials(context)) {
      TripManager.getInstance().invalidate(ValidationStep.USER_LOGOUT);
      Answers.getInstance().logCustom(new CustomEvent("new ping call, no credentials"));
      return;
    }

    DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
      @Override
      public void run(Driver driver) {
        Location lastKnownLocation = manager.getLastKnownLocation();
        Integer tripId = TripManager.getInstance().getTripId();
        Vehicle currentVehicle = DriverManager.getInstance().getCurrentVehicle();

        if (lastKnownLocation != null
          && tripId != null
          && currentVehicle != null
          && currentVehicle.vehicleId != null) {

          final Ping newPing = new Ping(
            lastKnownLocation,
            tripId,
            currentVehicle.vehicleId,
            driver.sessionId,
            currentVehicle.medallion
          );

          EventBus.getDefault().post(new PingCreated(newPing));

          if (PingKiller.getInstance().shouldKillPings()) {
            appendStrip(newPing);

          } else {
            SfoApi.getInstance().ping(
              newPing.tripId,
              newPing.geofenceStatus,
              newPing.latitude,
              newPing.longitude,
              newPing.medallion,
              newPing.sessionId,
              newPing.timestamp,
              newPing.tripId,
              newPing.vehicleId
            ).enqueue(new Callback<Void>() {
              @Override
              public void onResponse(Call<Void> call, Response<Void> response) {
                EventBus.getDefault().post(new NetworkResponse(response));
                if (!response.isSuccessful()) {
                  appendStrip(newPing);
                }
              }

              @Override
              public void onFailure(Call<Void> call, Throwable t) {
                appendStrip(newPing);
                EventBus.getDefault().post(new NetworkResponse(t));
              }
            });
          }
        } else {
          Answers.getInstance().logCustom(new CustomEvent("new ping call, invalid location, trip," +
            " or vehicle"));
          Log.e("error", "invalid location, trip, or vehicle");
        }
      }
    });
  }

  void sendOldPings() {
    sendOldPings(TripManager.getInstance().getTripId());
  }

  public void sendOldPings(final Integer tripId) {

    final PingBatch pingBatch = getPingBatch();

    if (pingBatch != null
      && tripId != null) {

      missedPings.clear();

      if (PingKiller.getInstance().shouldKillPings()) {
        missedPings.addAll(pingBatch.pings);

      } else {

        DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
          @Override
          public void run(Driver driver) {
            SfoApi.getInstance().pings(tripId, pingBatch)
              .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                  EventBus.getDefault().post(new NetworkResponse(response));
                  if (!response.isSuccessful()) {
                    missedPings.addAll(pingBatch.pings);
                  }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                  missedPings.addAll(pingBatch.pings);
                  EventBus.getDefault().post(new NetworkResponse(t));
                }
              });
          }
        });
      }
    }
  }

  private void appendStrip(Ping ping) {
    ping.medallion = null;
    ping.sessionId = null;
    ping.tripId = null;
    ping.vehicleId = null;
    missedPings.add(ping);
  }

  private PingBatch getPingBatch() {

    Driver driver = DriverManager.getInstance(context).getCurrentDriver();

    if (driver != null
      && driver.sessionId != null
      && missedPings.size() > 0) {

      return new PingBatch(missedPings, driver.sessionId);
    }

    return null;
  }
}
