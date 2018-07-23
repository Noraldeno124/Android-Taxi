package com.flysfo.shorttrips.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.driver.DriverCredential;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.notification.NotificationManager;
import com.flysfo.shorttrips.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mattluedke on 1/4/16.
 */
public class AuthActivity extends Activity {

  @BindView(R.id.edittext_username)
  EditText usernameEditText;

  @BindView(R.id.edittext_password)
  EditText passwordEditText;

  @BindView(R.id.btn_login)
  Button btnLogin;

  @BindView(R.id.loading_spinner)
  ProgressBar loadingSpinner;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DriverCredential credential = DriverCredential.load(this);
    if (credential != null) {
      setContentView(R.layout.activity_auth_loading);
      login(credential, this);
    } else {
      setContentView(R.layout.activity_auth);
      ButterKnife.bind(this);

      passwordEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (actionId == EditorInfo.IME_ACTION_DONE) {
            login(new DriverCredential(usernameEditText.getText().toString(),
              passwordEditText.getText().toString(), AuthActivity.this), AuthActivity.this);
            return true;
          }
          return false;
        }
      });
    }
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void login(final DriverCredential credential, final Activity activity) {

    boolean valid = true;

    if (credential.username == null || credential.username.isEmpty()) {
      valid = false;
      usernameEditText.setError( "Username is required" );
    }

    if (credential.password == null || credential.password.isEmpty()) {
      valid = false;
      passwordEditText.setError( "Password is required" );
    }

    if (valid) {

      if (!Util.internetConnected(this)) {
        Toast.makeText(
            getApplicationContext(),
            R.string.please_check_internet_connection,
            Toast.LENGTH_LONG
        ).show();
        return;
      }

      if (btnLogin != null && loadingSpinner != null) {
        btnLogin.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);
      }

      SfoApi.getInstance()
          .authenticateDriver(credential.username, credential.password, credential.latitude,
              credential.longitude, credential.deviceUuid, credential.osVersion, credential.deviceOs)
          .enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {

              if (response == null
                  || response.body() == null
                  || response.body().response == null) {

                Toast.makeText(getApplicationContext(), R.string.unsuccessful_login, Toast.LENGTH_LONG)
                    .show();
                if (btnLogin != null && loadingSpinner != null) {
                  btnLogin.setVisibility(View.VISIBLE);
                  loadingSpinner.setVisibility(View.GONE);
                }
                return;
              }

              Context context = AuthActivity.this;
              credential.save(context);
              DriverManager.getInstance(context).setCurrentDriver(response.body().response);
              NotificationManager.refreshAll(context);
              startActivity(new Intent(activity, MainActivity.class));
              activity.finish();
            }

            @Override
            public void onFailure(Call<DriverResponse> call, Throwable t) {
              Toast.makeText(getApplicationContext(), R.string.unsuccessful_login, Toast.LENGTH_LONG)
                  .show();
              Log.e("error", t.getLocalizedMessage());
              if(btnLogin != null && loadingSpinner != null) {
                btnLogin.setVisibility(View.VISIBLE);
                loadingSpinner.setVisibility(View.GONE);
              } else {
                Activity activity = AuthActivity.this;
                DriverCredential.clear(activity);
                activity.finish();
                startActivity(new Intent(activity, AuthActivity.class));
              }
            }
          });
    }
  }

  @OnClick(R.id.btn_login)
  public void triggerLogin() {
    login(new DriverCredential(usernameEditText.getText().toString(),
        passwordEditText.getText().toString(), this), this);
  }
}
