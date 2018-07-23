package com.flysfo.shorttrips.handlers;

import com.flysfo.shorttrips.avi.AviManager;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.model.antenna.Antenna;
import com.flysfo.shorttrips.model.antenna.AntennaResponse;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.driver.Vehicle;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 2/11/16.
 */
public class CheckingForExitAvi implements ContextHandler {
  @Override
  public void call(StatefulContext context) throws Exception {
    if (!Util.TESTING) {
      check();
    }
  }

  private void check() {

    final Vehicle vehicle = DriverManager.getInstance().getCurrentVehicle();
    if (vehicle != null) {

      DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
        @Override
        public void run(DriverResponse.Driver driver) {
          SfoApi.getInstance().getAntenna(vehicle.transponderId)
            .enqueue(new Callback<AntennaResponse>() {
              @Override
              public void onResponse(Call<AntennaResponse> call, Response<AntennaResponse>
                aviResponse) {
                EventBus.getDefault().post(new NetworkResponse(aviResponse));

                if (aviResponse.body() != null && aviResponse.body().response != null &&
                  aviResponse.body().response.device() != null) {

                  Antenna antenna = aviResponse.body().response;
                  GtmsLocation foundLocation = antenna.device();

                  AviManager.getInstance().setLatestAviLocation(foundLocation);

                  if (foundLocation == GtmsLocation.DOM_EXIT
                    || foundLocation == GtmsLocation.INTL_ARRIVAL_EXIT) {

                    TripManager.getInstance().setStartTime(antenna.aviDate);
                  }
                }

                StateManager.getInstance().trigger(Event.EXIT_AVI_CHECK_COMPLETE);
              }

              @Override
              public void onFailure(Call<AntennaResponse> call, Throwable t) {
                EventBus.getDefault().post(new NetworkResponse(t));
                StateManager.getInstance().trigger(Event.EXIT_AVI_CHECK_COMPLETE);
              }
            });
        }
      });
    } else {
      throw new RuntimeException("can't call this without a vehicle");
    }
  }
}
