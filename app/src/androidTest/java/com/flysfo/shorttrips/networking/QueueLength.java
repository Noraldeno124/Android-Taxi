package com.flysfo.shorttrips.networking;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.queue.QueueLengthResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/9/16.
 */
public class QueueLength extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public QueueLength() {
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

  public void testRequestQueueLength() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .requestQueueLength()
        .enqueue(new Callback<QueueLengthResponse>() {
          @Override
          public void onResponse(Call<QueueLengthResponse>
                                     call, Response<QueueLengthResponse> response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<QueueLengthResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }

  public void testRequestLotCapacity() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .requestLotCapacity()
        .enqueue(new Callback<Integer>() {
          @Override
          public void onResponse(Call<Integer> call, Response<Integer> response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<Integer> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }


}