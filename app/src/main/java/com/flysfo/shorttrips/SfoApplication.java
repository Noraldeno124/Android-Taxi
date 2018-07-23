package com.flysfo.shorttrips;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.driver.DriverCredential;
import com.flysfo.shorttrips.notification.NotificationManager;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mattluedke on 2/12/16.
 */
public class SfoApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    NotificationManager.register(this);

    Fabric.with(this, new Crashlytics());

    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build()
    );
  }

  // to test, run:
  // adb shell am send-trim-memory com.flysfo.shorttrips.debug RUNNING_CRITICAL
  // doesn't work for TRIM_MEMORY_MODERATE, unfortunately
  @Override
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);

    if (level >= TRIM_MEMORY_MODERATE) {

      Answers.getInstance().logCustom(new CustomEvent("onTrimMemory low"));

      if (SfoPreferences.getRestartPreference(this)
          && DriverCredential.loggedIn(this)) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

      } else {
        Answers.getInstance().logCustom(new CustomEvent("onTrimMemory critical"));
        StateManager.getInstance().trigger(Event.APP_QUIT);
      }
    }
  }
}
