package com.flysfo.shorttrips.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.geofence.GeofenceManager;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.ping.PingManager;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Speaker;

/**
 * Created by mattluedke on 3/1/16.
 */
public class OnClearFromRecentService extends Service {

  // holding references so everything stays alive
  StateManager stateManager;
  GeofenceManager geofenceManager;
  TripManager tripManager;
  Speaker speaker;
  SfoLocationManager locationManager;
  PingManager pingManager;
  DriverManager driverManager;

  private static final int SERVICE_ID = 1979;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    stateManager = StateManager.getInstance();
    geofenceManager = GeofenceManager.getInstance();
    tripManager = TripManager.getInstance();
    speaker = Speaker.getInstance(this);
    locationManager = SfoLocationManager.getInstance();
    pingManager = PingManager.getInstance(this);
    driverManager = DriverManager.getInstance(this);

    Intent notificationIntent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    Notification notification = new Notification.Builder(this)
        .setSmallIcon(R.drawable.sfo_logo_alpha)
        .setContentTitle(getText(R.string.app_name))
        .setContentText(getText(R.string.notification))
        .setWhen(System.currentTimeMillis())
        .setContentIntent(pendingIntent)
        .build();

    startForeground(SERVICE_ID, notification);

    if (SfoPreferences.hasPendingAppQuit(getApplicationContext())) {
      TripManager.getInstance().invalidate(ValidationStep.APP_QUIT, new InvalidateCallback() {
        @Override
        public void invalidated() {
          StateManager.getInstance().trigger(Event.APP_QUIT);
          SfoPreferences.setPendingAppQuit(getApplicationContext(), false);
        }
      });
    }

    return START_NOT_STICKY;
  }

  public void onTaskRemoved(Intent rootIntent) {
    SfoPreferences.setPendingAppQuit(getApplicationContext(), true);
    SfoPreferences.setPendingFailure(getApplicationContext(), ValidationStep.UNSPECIFIED);
    SfoPreferences.setPendingSuccess(getApplicationContext(), null);
    PingManager.getInstance(getApplicationContext());
    TripManager.getInstance().invalidate(ValidationStep.APP_QUIT, new InvalidateCallback() {
      @Override
      public void invalidated() {
        StateManager.getInstance().trigger(Event.APP_QUIT);
        stopSelf();
        SfoPreferences.setPendingAppQuit(getApplicationContext(), false);
        DriverManager.getInstance(getApplicationContext()).setCurrentDriver(null);
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    try {
      Speaker.getInstance().stop();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }
}
