package com.flysfo.shorttrips.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.LocationRead;
import com.flysfo.shorttrips.events.LocationStatusUpdated;
import com.flysfo.shorttrips.geofence.GeofenceManager;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SfoLocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
    .OnConnectionFailedListener {

  private static final float REQUIRED_ACCURACY = 100; // meters

  private Activity activity;
  private GoogleApiClient googleApiClient;
  private Location lastKnownLocation;
  private LocationRequest locationRequest;

  public Boolean debuggingLocation = false;

  private Boolean mockingFoundToBeOn = false;

  private static SfoLocationManager instance = null;

  private ContentObserver locationServiceObserver = new ContentObserver(null) {
    @Override
    public void onChange(boolean selfChange) {
      super.onChange(selfChange);
      checkLocation(null);
    }
  };

  private final long DELAY_BEFORE_RECONNECTING = 1000;
  private final Handler handler = new Handler();
  private final Runnable reconnector = new Runnable() {
    @Override
    public void run() {
      start();
    }
  };

  public void checkLocation(LocationAvailability locationAvailability) {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
          .ACCESS_FINE_LOCATION}, MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
      invalidate();
      return;
    }

    if (((LocationManager) activity.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)
        && !mockingFoundToBeOn
        && !Util.isAirplaneModeOn(activity)) {

      if (StateManager.getInstance().getState() == State.GPS_IS_OFF) {
        EventBus.getDefault().post(new LocationStatusUpdated(true));
        StateManager.getInstance().trigger(Event.GPS_ENABLED);
        start();
      }

    } else {
      invalidate();

      if (MainActivity.active
          && !((LocationManager) activity.getSystemService(Context
          .LOCATION_SERVICE))
          .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.gps_required);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
          }
        });
        builder.setCancelable(false);
        builder.create().show();

      } else if (MainActivity.active && mockingFoundToBeOn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.dont_mock_me);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();

      } else if (MainActivity.active && Util.isAirplaneModeOn(activity)) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.airplane_mode_off);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
          }
        });
        builder.setCancelable(false);
        builder.create().show();
      }
    }
  }

  private LocationCallback locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult result) {
      super.onLocationResult(result);

      Location location = result.getLastLocation();

      Bundle extras = location.getExtras();
      boolean isMockLocation = extras != null && extras.getBoolean(FusedLocationProviderApi.KEY_MOCK_LOCATION, false);

      if (isMockLocation || location.isFromMockProvider()) {
        mockingFoundToBeOn = true;
        invalidate();
        return;
      } else {
        if (mockingFoundToBeOn) {
          mockingFoundToBeOn = false;
          checkLocation(null);
        }
      }

      if (!debuggingLocation) {
        if (location.getAccuracy() < REQUIRED_ACCURACY) {
          lastKnownLocation = location;
          EventBus.getDefault().post(new LocationRead(location));
        }
      }
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
      super.onLocationAvailability(locationAvailability);
      checkLocation(locationAvailability);
    }
  };

  public static SfoLocationManager getInstance() {
    if (instance == null) {
      throw new NullPointerException("trying to access SfoLocationManager before initialized");
    }
    return instance;
  }

  public static SfoLocationManager getInstance(Activity activity) {
    if (instance == null) {
      instance = new SfoLocationManager(activity);
    } else {
      instance.activity = activity;
    }
    return instance;
  }

  private SfoLocationManager(Activity activity) {
    EventBus.getDefault().register(this);
    this.activity = activity;
    googleApiClient = new GoogleApiClient.Builder(activity)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    locationRequest = new LocationRequest();
    locationRequest.setInterval(5000); // preferred rate
    locationRequest.setFastestInterval(1000); // fastest possible rate
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    activity.getContentResolver().registerContentObserver(
        Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED),
        false, locationServiceObserver);

    IntentFilter intentFilter = new IntentFilter("android.intent.action.AIRPLANE_MODE");

    BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        checkLocation(null);
      }
    };

    activity.registerReceiver(receiver, intentFilter);
  }

  public void start() {
    if (googleApiClient.isConnected()) {
      onConnected(new Bundle());
    } else {
      googleApiClient.connect();
    }
  }

  public static void invalidate() {
    EventBus.getDefault().post(new LocationStatusUpdated(false));
    TripManager.getInstance().invalidate(ValidationStep.GPS_FAILURE);
    StateManager.getInstance().trigger(Event.GPS_DISABLED);
  }

  private void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission
          .ACCESS_FINE_LOCATION}, MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
      return;
    }

    StateManager.getInstance().trigger(Event.GPS_ENABLED);
    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
        locationRequest, locationCallback, null);
  }

  public void stop() {
    GeofenceManager.getInstance(activity).reset();
    if (googleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationCallback);
    }
  }

  @Override
  public void onConnected(Bundle bundle) {

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest);

    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
        .checkLocationSettings(googleApiClient, builder.build());

    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
      @Override
      public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        // MCL: this next line is in the Google documentation but doesn't make sense
//        final LocationSettingsStates = locationSettingsResult.getLocationSettingsStates();

        switch (status.getStatusCode()) {
          case LocationSettingsStatusCodes.SUCCESS:

            // All location settings are satisfied. The client can
            // initialize location requests here.
            startLocationUpdates();
            break;

          case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            checkLocation(null);
            try {
              // Show the dialog by calling startResolutionForResult(),
              // and check the result in onActivityResult().
              status.startResolutionForResult(
                  activity,
                  MainActivity.REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException e) {
              // Ignore the error.
            }
            break;
          case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
            // Location settings are not satisfied. However, we have no way
            // to fix the settings so we won't show the dialog.
            checkLocation(null);
            break;
        }
      }
    });
  }

  @Override
  public void onConnectionSuspended(int i) {
    Answers.getInstance().logCustom(new CustomEvent("onConnectionSuspended " + i));
    handler.postDelayed(reconnector, DELAY_BEFORE_RECONNECTING);
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Answers.getInstance().logCustom(new CustomEvent("onConnectionFailed"));
    handler.postDelayed(reconnector, DELAY_BEFORE_RECONNECTING);
  }

  public Location getLastKnownLocation() {
    return lastKnownLocation;
  }

  @Subscribe
  public void locationRead(LocationRead event) {
    lastKnownLocation = event.location;
  }
}
