package com.flysfo.shorttrips.polling;

import com.flysfo.shorttrips.events.GtmsCallMade;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.lotcounter.LotCounterManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.driver.VehicleResponse;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidDriverRunnable;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract class AssociationPoller extends Poller {
  abstract void successLotCounterManager(DriverResponse.Driver driver);

  void action() {
    DriverManager.getInstance().callWithValidSession(new ValidDriverRunnable() {
      @Override
      public void run(DriverResponse.Driver driver) {
        Call<VehicleResponse> call = SfoApi.getInstance().getVehicle(driver.cardId);

        successLotCounterManager(driver);

        call.clone().enqueue(new Callback<VehicleResponse>() {
          @Override
          public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse>
            vehicleResponse) {
            EventBus.getDefault().post(new NetworkResponse(vehicleResponse));

            if (vehicleResponse.body() != null
              && vehicleResponse.body().response != null
              && vehicleResponse.body().response.isValid()) {

              DriverManager.getInstance().setCurrentVehicle(vehicleResponse.body().response);
            }
          }

          @Override
          public void onFailure(Call<VehicleResponse> call, Throwable t) {
            EventBus.getDefault().post(new NetworkResponse(t));
          }
        });

        EventBus.getDefault().post(new GtmsCallMade());

        calls.add(call);
      }
    });
  }
}
