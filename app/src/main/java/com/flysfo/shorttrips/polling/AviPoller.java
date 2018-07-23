package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.avi.AviManager;
import com.flysfo.shorttrips.events.GtmsCallMade;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.events.UnexpectedGtms;
import com.flysfo.shorttrips.model.antenna.AntennaResponse;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.driver.Vehicle;
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
public abstract class AviPoller extends Poller {

  abstract Long acceptableTimeDifference();

  abstract GtmsLocation[] expectedAvis();

  abstract Event successEvent();

  abstract void callbackLotCounterManager(DriverResponse.Driver driver);

  private String[] expectedAviNames() {
    String[] names = new String[expectedAvis().length];

    for (int i = 0; i < expectedAvis().length; i++) {
      names[i] = expectedAvis()[i].name();
    }

    return names;
  }

  void action() {

    final Vehicle vehicle = DriverManager.getInstance().getCurrentVehicle();
    if (vehicle != null) {

      DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
        @Override
        public void run(final DriverResponse.Driver driver) {
          Call<AntennaResponse> call = SfoApi.getInstance().getAntenna(vehicle.transponderId);
          call.clone().enqueue(new Callback<AntennaResponse>() {
            @Override
            public void onResponse(Call<AntennaResponse> call, Response<AntennaResponse> aviResponse) {
              EventBus.getDefault().post(new NetworkResponse(aviResponse));

              if (aviResponse.body() != null && aviResponse.body().response != null &&
                aviResponse.body().response.device() != null) {

                GtmsLocation foundLocation = aviResponse.body().response.device();
                Date aviDate = aviResponse.body().response.aviDate;

                AviManager.getInstance().setLatestAviLocation(foundLocation);
                callbackLotCounterManager(driver);

                for (GtmsLocation location : expectedAvis()) {
                  if (foundLocation == location
                    && (acceptableTimeDifference() == null
                    || System.currentTimeMillis() - aviDate.getTime() < acceptableTimeDifference())) {
                    StateManager.getInstance().trigger(successEvent());
                    return;
                  }
                }

                EventBus.getDefault().post(new UnexpectedGtms(expectedAviNames(),
                  foundLocation.name()));
                checkForFallback();
              }
            }

            @Override
            public void onFailure(Call<AntennaResponse> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));
            }
          });

          EventBus.getDefault().post(new GtmsCallMade());

          calls.add(call);
        }
      });
    }
  }
}
