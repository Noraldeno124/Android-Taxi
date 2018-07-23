package com.flysfo.shorttrips.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by mattluedke on 2/12/16.
 */
public class Util {
  public static Boolean TESTING = false;

  public static int lastKnownMainScreen = 2;
  private static String lastUsedNotificationId = null;
  private static String lastKnownNotificationId = null;

  public static void setKnownId(String pushId) {
    lastKnownNotificationId = pushId;
  }

  public static boolean lastKnownPushIdIsNull() {
    return lastKnownNotificationId == null;
  }

  public static boolean startingFromPush() {
    return lastKnownNotificationId != null
      && (lastUsedNotificationId == null
        || lastUsedNotificationId.compareToIgnoreCase(lastKnownNotificationId) != 0);
  }

  public static void useKnownPushId() {
    lastUsedNotificationId = lastKnownNotificationId;
  }

  public static boolean isAirplaneModeOn(Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return Settings.System.getInt(context.getContentResolver(),
          Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    } else {
      return Settings.Global.getInt(context.getContentResolver(),
          Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
  }

  public static boolean internetConnected(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) {
      return false;
    }

    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null
      && networkInfo.isAvailable()
      && networkInfo.isConnected();
  }
}
