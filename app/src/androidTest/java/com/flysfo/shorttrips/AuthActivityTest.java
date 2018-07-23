package com.flysfo.shorttrips;

import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.auth.AuthActivity;
import com.flysfo.shorttrips.model.driver.DriverCredential;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.Url;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 1/4/16.
 */
public class AuthActivityTest extends ActivityInstrumentationTestCase2<AuthActivity> {

  AuthActivity mAuthActivity;

  public AuthActivityTest() {
    super(AuthActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!Url.isStaging()) {
      throw new RuntimeException("can't use production URL in tests");
    }

    mAuthActivity = getActivity();
  }

  // NETWORKING

  public void testDriverLogin() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    Looper.prepare();

    DriverCredential driverCredential = new DriverCredential("testdriver6", "testdriver6@", mAuthActivity);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .authenticateDriver(driverCredential.username, driverCredential.password,
            driverCredential.latitude, driverCredential.longitude, driverCredential.deviceUuid,
            driverCredential.osVersion, driverCredential.deviceOs)
        .enqueue(new Callback<DriverResponse>() {
          @Override
          public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
            request_succeeded[0] = response.body().response != null;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<DriverResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }
}
