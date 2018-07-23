package com.flysfo.shorttrips.notification;

import android.content.Context;

import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationManager {

  public static void register(Context context) {

    FirebaseApp.initializeApp(context);

    refreshAll(context);
  }

  public static void refreshAll(Context context) {
    for (NotificationType notificationType : NotificationType.values()) {
      refreshSubscription(context, notificationType);
    }
  }

  private static boolean getNotificationEnabled(Context context, NotificationType
    notificationType) {
    try {
      return SfoPreferences.getNotificationPref(context, notificationType.toString());
    } catch (Exception e) {
      setNotificationActive(context, notificationType, true);
      return true;
    }
  }

  public static void setNotificationActive(Context context, NotificationType notificationType,
                                           boolean on) {
    SfoPreferences.setBoolPref(context, notificationType.toString(), on);
    refreshSubscription(context, notificationType);
  }

  private static void refreshSubscription(Context context, NotificationType notificationType) {

    if (getNotificationEnabled(context, notificationType)
      && DriverManager.getInstance(context).getCurrentDriver() != null) {

      FirebaseMessaging.getInstance().subscribeToTopic(topicString(notificationType));

    } else {
      FirebaseMessaging.getInstance().unsubscribeFromTopic(topicString(notificationType));
    }
  }

  private static String topicString(NotificationType notificationType) {
    return "/topics/" + notificationType.toString();
  }
}
