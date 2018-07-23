package com.flysfo.shorttrips.util;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import com.flysfo.shorttrips.prefs.SfoPreferences;

/**
 * Created by mattluedke on 3/2/16.
 */
public class Speaker {

  private TextToSpeech textToSpeech;
  private Context context;

  private static Speaker instance = null;
  public static Speaker getInstance(Context context) {
    if (instance == null) {
      instance = new Speaker(context);
    } else {
      instance.textToSpeech = new TextToSpeech(context, null);
      instance.context = context;
    }
    return instance;
  }

  public static Speaker getInstance() {
    if (instance == null) {
      throw new NullPointerException("trying to access Speaker before initialized");
    }
    return instance;
  }

  private Speaker(Context context) {
    this.textToSpeech = new TextToSpeech(context, null);
    this.context = context;
  }

  public void stop() {
    this.textToSpeech.shutdown();
    this.context = null;
  }

  public void speak(int stringRes) {
    if (context != null && SfoPreferences.getAudioPreference(context)) {

      String string = context.getString(stringRes);

      string = string.replace("SFO", "S. F. Oh, ");
      string = string.replace("required", "required, ");
      string = string.replace("geofence", "G O fence");

      if (!textToSpeech.isSpeaking()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
          textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null);
        }
      }
    }
  }
}
