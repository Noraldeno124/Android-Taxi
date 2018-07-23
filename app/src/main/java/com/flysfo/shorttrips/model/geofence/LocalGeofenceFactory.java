package com.flysfo.shorttrips.model.geofence;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;

import java.io.InputStream;

/**
 * Created by mattluedke on 2/3/16.
 */
public class LocalGeofenceFactory {

  public static LocalGeofence make(Context context, int jsonResId, SfoGeofence sfoGeofence) {
    String jsonString = "";

    try {
      Resources res = context.getResources();
      InputStream in_s = res.openRawResource(jsonResId);

      byte[] b = new byte[in_s.available()];
      //noinspection ResultOfMethodCallIgnored
      in_s.read(b);
      jsonString = new String(b);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Gson gson = new Gson();
    LocalGeofence localGeofence = gson.fromJson(jsonString, LocalGeofence.class);

    localGeofence.sfoGeofence = sfoGeofence;

    return localGeofence;
  }
}
