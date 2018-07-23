package com.flysfo.shorttrips.model.driver;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.prefs.SfoPreferences;

/**
 * Created by mattluedke on 12/14/15.
 */
public class DriverCredential {

  public static class Fields {
    public static final String DEVICE_UUID = "device_uuid";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DRIVER_DEVICE_OS = "device_os";
    public static final String OS_VERSION = "os_version";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
  }

  public final String deviceOs = "android";
  public final String deviceUuid = Build.SERIAL;
  public Double latitude;
  public Double longitude;
  public final String osVersion = Build.VERSION.RELEASE;
  public final String password;
  public final String username;

  public DriverCredential(String username, String password, Activity activity) {
    this.username = username;
    this.password = password;
    if (activity != null) {
      Location location = SfoLocationManager.getInstance(activity).getLastKnownLocation();
      if (location != null) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
      }
    }
  }

  public static boolean loggedIn(Context context) {
    return SfoPreferences.containsCredentials(context);
  }

  public static DriverCredential load(Context context) {
    Pair<String, String> credentials = SfoPreferences.getCredentials(context);
    String username = credentials.first;
    String password = credentials.second;
    if (username != null && password != null) {
      if (context instanceof Activity) {
        return new DriverCredential(username, password, (Activity) context);
      } else {
        return new DriverCredential(username, password, null);
      }
    } else {
      return null;
    }
  }

  public void save(Context context) {
    DriverCredential.clear(context);
    SfoPreferences.saveCredentials(context, username, password);
    Crashlytics.setUserIdentifier(username);
  }

  public static void clear(Context context) {
    SfoPreferences.clearCredentials(context);
  }
}
