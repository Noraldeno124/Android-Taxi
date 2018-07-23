package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.events.GtmsCallMade;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.events.UnexpectedGtms;
import com.flysfo.shorttrips.model.CidResponse;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/9/16.
 */
public abstract class CidPoller extends Poller {

  abstract GtmsLocation[] expectedCids();

  abstract Event successEvent();

  abstract long acceptableCidAge();

  private String[] expectedCidNames() {
    String[] names = new String[expectedCids().length];

    for (int i = 0; i < expectedCids().length; i++) {
      names[i] = expectedCids()[i].name();
    }

    return names;
  }

  void action() {
    DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
      @Override
      public void run(DriverResponse.Driver driver) {
        Call<CidResponse> call = SfoApi.getInstance().fetchMostRecentCid(driver.driverId);

        call.clone().enqueue(new Callback<CidResponse>() {
          @Override
          public void onResponse(Call<CidResponse> call, Response<CidResponse> cidResponse) {
            EventBus.getDefault().post(new NetworkResponse(cidResponse));

            if (cidResponse.body() != null
              && cidResponse.body().response != null
              && cidResponse.body().response.device() != null
              && cidResponse.body().response.cidTimeRead != null) {

              for (GtmsLocation location : expectedCids()) {

                if (cidResponse.body().response.device() == location
                  && cidResponse.body().response.cidTimeRead.getTime() >
                  new Date().getTime() - acceptableCidAge()) {

                  StateManager.getInstance().trigger(successEvent());
                  return;
                }
              }

              EventBus.getDefault().post(new UnexpectedGtms(expectedCidNames(),
                cidResponse.body().response.device().name()));
              checkForFallback();
            }
          }

          @Override
          public void onFailure(Call<CidResponse> call, Throwable t) {
            EventBus.getDefault().post(new NetworkResponse(t));
          }
        });

        EventBus.getDefault().post(new GtmsCallMade());

        calls.add(call);
      }
    });
  }
}
