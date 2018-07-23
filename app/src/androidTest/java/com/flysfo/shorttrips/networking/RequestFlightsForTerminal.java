package com.flysfo.shorttrips.networking;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.model.flight.FlightResponse;
import com.flysfo.shorttrips.model.terminal.TerminalSummaryResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/9/16.
 */
public class RequestFlightsForTerminal extends ActivityInstrumentationTestCase2<MainActivity> {

  MainActivity mainActivity;

  public RequestFlightsForTerminal() {
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

  public void testRequestArrivalFlightsForTerminal() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .requestArrivalFlightsForTerminal(200, 100)
        .enqueue(new Callback<FlightResponse>() {
          @Override
          public void onResponse(Call<FlightResponse> call, Response<FlightResponse>
              response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<FlightResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }

  public void testRequestDepartureFlightsForTerminal() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .requestDepartureFlightsForTerminal(200, 300)
        .enqueue(new Callback<FlightResponse>() {
          @Override
          public void onResponse(Call<FlightResponse> call, Response<FlightResponse>
              response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<FlightResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }

  public void testRequestArrivalTerminalSummaries() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .requestArrivalTerminalSummaries(1)
        .enqueue(new Callback<TerminalSummaryResponse>() {
          @Override
          public void onResponse
              (Call<TerminalSummaryResponse> call, Response<TerminalSummaryResponse>
                  response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<TerminalSummaryResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }

  public void testRequestDepartureTerminalSummaries() throws InterruptedException {

    final CountDownLatch waiter = new CountDownLatch(1);

    final boolean[] request_succeeded = {false};

    SfoApi.getInstance()
        .requestDepartureTerminalSummaries(2)
        .enqueue(new Callback<TerminalSummaryResponse>() {
          @Override
          public void onResponse
              (Call<TerminalSummaryResponse> call, Response<TerminalSummaryResponse>
                  response) {
            request_succeeded[0] = true;
            waiter.countDown();
          }

          @Override
          public void onFailure(Call<TerminalSummaryResponse> call, Throwable t) {
            request_succeeded[0] = false;
            waiter.countDown();
          }
        });

    waiter.await(10, TimeUnit.SECONDS);
    assertTrue(request_succeeded[0]);
  }
}
