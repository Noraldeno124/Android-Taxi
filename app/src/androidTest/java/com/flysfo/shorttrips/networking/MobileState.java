package com.flysfo.shorttrips.networking;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/9/16.
 */
public class MobileState extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public MobileState() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!Url.isStaging()) {
      throw new RuntimeException("can't use production URL in tests");
    }

    mainActivity = getActivity();
  }

  public void testUpdateMobileState() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .updateMobileState(1, 12.0, 13.0, 2, 3)
        .enqueue(new Callback<Void>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }
}
