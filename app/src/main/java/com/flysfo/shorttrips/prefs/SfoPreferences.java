package com.flysfo.shorttrips.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.trip.ValidationStep;

/**
 * Created by mattluedke on 1/4/16.
 */
public class SfoPreferences {

  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";
  private static final String TERMS = "terms";
  private static final String PENDING_APP_QUIT = "pending_app_quit";
  private static final String PENDING_SUCCESS = "pending_success";
  private static final String PENDING_FAILURE = "pending_failure";

  private static SharedPreferences get(Context context) {
    return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
  }

  public static boolean containsCredentials(Context context) {
    SharedPreferences prefs = get(context);
    return prefs.contains(USERNAME_KEY) && prefs.contains(PASSWORD_KEY);
  }

  public static Pair<String, String> getCredentials(Context context) {
    return new Pair<>(
      get(context).getString(USERNAME_KEY, null),
      get(context).getString(PASSWORD_KEY, null)
    );
  }

  public static void saveCredentials(Context context, String username, String password) {
    get(context).edit().putString(USERNAME_KEY, username).putString(PASSWORD_KEY, password).apply();
  }

  public static void clearCredentials(Context context) {
    get(context).edit().remove(USERNAME_KEY).remove(PASSWORD_KEY).apply();
  }

  public static void saveTerms(String terms, Context context) {
    get(context).edit().putString(TERMS, terms).apply();
  }

  public static String getTerms(Context context) {
    return get(context).getString(TERMS, "");
  }

  public static void setAudioPreference(Context context, boolean setting) {
    get(context)
        .edit()
        .putBoolean(context.getString(R.string.audio_setting_title), setting)
        .apply();
  }

  public static boolean getAudioPreference(Context context) {
    return get(context).getBoolean(context.getString(R.string.audio_setting_title), true);
  }

  public static boolean getRestartPreference(Context context) {
    return get(context).getBoolean(context.getString(R.string.automatic_restart), true);
  }

  public static void toggleRestartPreference(Context context, boolean setting) {
    get(context).edit().putBoolean(context.getString(R.string.automatic_restart), setting).apply();
  }

  public static void setPendingAppQuit(Context context, boolean pendingAppQuit) {
    get(context).edit().putBoolean(PENDING_APP_QUIT, pendingAppQuit).apply();
  }

  public static boolean hasPendingAppQuit(Context context) {
    return get(context).getBoolean(PENDING_APP_QUIT, false);
  }

  public static void setPendingSuccess(Context context, String pendingSuccess) {
    get(context).edit().putString(PENDING_SUCCESS, pendingSuccess).apply();
  }

  public static String pendingSuccess(Context context) {
    return get(context).getString(PENDING_SUCCESS, null);
  }

  public static void setPendingFailure(Context context, ValidationStep pendingFailure) {
    get(context).edit().putInt(PENDING_FAILURE, pendingFailure.toInt()).apply();
  }

  public static ValidationStep pendingFailure(Context context) {
    return ValidationStep.fromInt(get(context).getInt(PENDING_FAILURE, 0));
  }

  public static void setBoolPref(Context context, String key, boolean on) {
    get(context).edit().putBoolean(key, on).apply();
  }

  public static boolean getNotificationPref(Context context, String key) throws Exception {
    SharedPreferences prefs = get(context);
    if (!prefs.contains(key)) {
      throw new Exception("no pref");
    } else {
      return get(context).getBoolean(key, false);
    }
  }
}
