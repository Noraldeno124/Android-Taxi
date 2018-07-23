package com.flysfo.shorttrips.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pierreexygy on 3/7/16.
 */
public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent startingIntent = getIntent();
    if (startingIntent != null
      && startingIntent.getAction().compareToIgnoreCase("cone_notification") == 0) {

      if (Util.lastKnownPushIdIsNull()) {
        String messageId = (String) startingIntent.getExtras().get("google.message_id");
        Util.setKnownId(messageId);
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
      checkVersion();
  }

  private void sendToGooglePlay() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.app_out_of_date);
    builder.setMessage(R.string.redirect_to_google_play);
    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        final String appPackageName = getPackageName();
        try {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
      }
    });
    builder.setCancelable(false);
    try {
      builder.create().show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void versionCheckFailedError() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.error);
    builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        checkVersion();
      }
    });
    builder.setCancelable(false);
    try {
      builder.create().show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void versionCheckFailedNetwork() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.please_check_internet_connection);
    builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        checkVersion();
      }
    });
    builder.setCancelable(false);
    try {
      builder.create().show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void termCheckFailed() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.terms_failed);
    builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        checkTerms();
      }
    });
    builder.setCancelable(false);
    try {
      builder.create().show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void displayTerms(final String terms) {

    LayoutInflater inflater = LayoutInflater.from(this);
    View view = inflater.inflate(R.layout.alert_terms, null);
    TextView textview = (TextView) view.findViewById(R.id.text_terms);
    textview.setText(terms);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.terms);
    builder.setView(view);
    builder.setPositiveButton(R.string.i_agree, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        SfoPreferences.saveTerms(terms, SplashActivity.this);
        termsOK();
      }
    });
    builder.setCancelable(false);
    try {
      builder.create().show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void checkVersion() {

    PackageInfo pInfo = null;
    try {
      pInfo = getPackageManager().getPackageInfo
          (getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      versionCheckFailedError();
    }

    if (pInfo == null) {
      versionCheckFailedError();

    } else {

      if (!Util.internetConnected(this)) {
        versionCheckFailedNetwork();
        return;
      }

      final Double version = Double.valueOf(pInfo.versionName);

      SfoApi.getInstance()
          .getVersion("android")
          .enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {

              if (response.body() != null) {
                if (version >= response.body()) {
                  checkTerms();
                } else {
                  sendToGooglePlay();
                }
              } else {
                versionCheckFailedError();
              }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
              versionCheckFailedError();
            }
          });
    }
  }

  private void checkTerms() {

    SfoApi.getInstance()
        .getTerms("android")
        .enqueue(new Callback<String>() {
          @Override
          public void onResponse(Call<String> call, Response<String> response) {

            if (response.body() != null) {
              if (SfoPreferences.getTerms(SplashActivity.this).replaceAll("\\s+", "")
                  .compareToIgnoreCase(response.body().replaceAll("\\s+", "")) == 0) {

                termsOK();

              } else {
                displayTerms(response.body().trim());
              }
            } else {
              termCheckFailed();
            }
          }

          @Override
          public void onFailure(Call<String> call, Throwable t) {
            termCheckFailed();
          }
        });
  }

  private void termsOK() {
    startActivity(new Intent(this, AuthActivity.class));
    finish();
  }
}
