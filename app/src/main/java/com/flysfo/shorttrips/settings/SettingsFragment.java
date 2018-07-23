package com.flysfo.shorttrips.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.auth.AuthActivity;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.driver.DriverCredential;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.notification.NotificationManager;
import com.flysfo.shorttrips.notification.NotificationType;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.service.OnClearFromRecentService;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;

public class SettingsFragment extends PreferenceFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    final CheckBoxPreference audioPref = (CheckBoxPreference)findPreference(getString(R.string
        .audio_setting_title));
    audioPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (newValue instanceof Boolean) {
          SfoPreferences.setAudioPreference(getActivity(), (Boolean) newValue);
        }

        return true;
      }
    });

    final CheckBoxPreference coneNotificationPref = (CheckBoxPreference)findPreference(getString
      (R.string.cone_notification));
    coneNotificationPref.setOnPreferenceChangeListener(new NotificationChangeListener(NotificationType.CONE));

//    final CheckBoxPreference debugNotificationPref = (CheckBoxPreference)findPreference(getString
//      (R.string.debug_notification));
//    debugNotificationPref.setOnPreferenceChangeListener(new NotificationChangeListener
//      (NotificationType.DEBUG));
//
//    final CheckBoxPreference localdevNotificationPref = (CheckBoxPreference)findPreference(getString
//      (R.string.localdev_notification));
//    localdevNotificationPref.setOnPreferenceChangeListener(new NotificationChangeListener
//      (NotificationType.LOCAL_DEV));

    final CheckBoxPreference automaticRestartPref = (CheckBoxPreference)findPreference(getString(R.string
        .automatic_restart));
    automaticRestartPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(final Preference preference, Object newValue) {
        if (newValue instanceof Boolean) {
          if (!((Boolean) newValue)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.are_you_sure);
            builder.setMessage(R.string.automatic_restart_warning);
            builder.setCancelable(false);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                SfoPreferences.toggleRestartPreference(getActivity(), false);
                automaticRestartPref.setChecked(false);
              }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                SfoPreferences.toggleRestartPreference(getActivity(), true);
                automaticRestartPref.setChecked(true);
              }
            });
            try {
              builder.create().show();
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            SfoPreferences.toggleRestartPreference(getActivity(), true);
            automaticRestartPref.setChecked(true);
          }
        }

        return true;
      }
    });

    findPreference(getString(R.string.feedback)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"taxiops@flysfo.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context
            .TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();

        PackageInfo pInfo = null;
        try {
          pInfo = getActivity().getPackageManager().getPackageInfo
              (getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
        }

        i.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n"
                + Build.BRAND + ","
                + Build.MANUFACTURER + ","
                + Build.DEVICE + ","
                + Build.MODEL + ","
                + "android,"
                + Build.VERSION.RELEASE + ","
                + Build.VERSION.SDK_INT + ","
                + carrierName + ","
                + (pInfo != null ? pInfo.versionName : "")
        );
        try {
          startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
          Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT)
              .show();
        }

        return true;
      }
    });

    DriverCredential credential = DriverCredential.load(getActivity());
    PackageInfo pInfo = null;
    try {
      pInfo = getActivity().getPackageManager().getPackageInfo
          (getActivity().getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    if (credential != null && pInfo != null) {
      findPreference(getString(R.string.logged_in)).setTitle(String.format(getString(R.string
          .logged_in_as), credential.username, pInfo.versionName));
    }

    findPreference(getString(R.string.logout)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.are_you_sure);
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            logout();
          }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
        return true;
      }
    });
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    if (view != null) {
      view.setBackgroundColor(Color.WHITE);
    }
    return view;
  }

  private void logout() {
    Activity activity = getActivity();

    boolean originalSetting = SfoPreferences.getAudioPreference(activity);
    if (TripManager.getInstance().getTripId() == null) {
      SfoPreferences.setAudioPreference(activity, false);
    }

    TripManager.getInstance().invalidate(ValidationStep.USER_LOGOUT);
    StateManager.getInstance().trigger(Event.LOGGED_OUT);
    DriverCredential.clear(activity);
    DriverManager.getInstance(activity).setCurrentDriver(null);
    DriverManager.getInstance(activity).setCurrentVehicle(null);
    SfoLocationManager.getInstance(activity).stop();
    NotificationManager.refreshAll(activity);

    SfoPreferences.setAudioPreference(activity, originalSetting);

    activity.stopService(new Intent(activity, OnClearFromRecentService.class));
    activity.finish();
    startActivity(new Intent(activity, AuthActivity.class));
  }

  private class NotificationChangeListener implements Preference.OnPreferenceChangeListener {

    private NotificationType notificationType;

    NotificationChangeListener(NotificationType notificationType) {
      this.notificationType = notificationType;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

      if (newValue instanceof Boolean) {
        Boolean on = (Boolean) newValue;
        NotificationManager.setNotificationActive(getActivity(), notificationType, on);
        SfoPreferences.setBoolPref(getActivity(), notificationType.toString(), on);
      }

      return true;
    }
  }
}
