package com.flysfo.shorttrips.networking;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.antenna.AntennaResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/9/16.
 */
public class MostRecentAviTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public MostRecentAviTest() {
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

  public void testAviFetch() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .getAntenna(2005887)
        .enqueue(new Callback<AntennaResponse>() {
          @Override
          public void onResponse(Call<AntennaResponse> call, Response<AntennaResponse> response) {
            request_succeeded[0] = response.body().response != null;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<AntennaResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }
}
