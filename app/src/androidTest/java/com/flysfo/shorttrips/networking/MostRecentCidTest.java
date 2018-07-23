package com.flysfo.shorttrips.networking;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.CidResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/9/16.
 */
public class MostRecentCidTest extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public MostRecentCidTest() {
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

  public void testCidFetch() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .fetchMostRecentCid(255276)
        .enqueue(new Callback<CidResponse>() {
          @Override
          public void onResponse(Call<CidResponse> call, Response<CidResponse> response) {
            request_succeeded[0] = response.body().response != null;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<CidResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }
}
