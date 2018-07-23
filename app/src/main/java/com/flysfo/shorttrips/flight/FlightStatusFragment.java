package com.flysfo.shorttrips.flight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.ReachabilityEvent;
import com.flysfo.shorttrips.model.flight.FlightResponse;
import com.flysfo.shorttrips.model.flight.FlightType;
import com.flysfo.shorttrips.model.terminal.TerminalId;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.refresh.TimerListener;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pierreexygy on 3/16/16.
 */
public class FlightStatusFragment extends Fragment {
  private TerminalId terminalId;
  private Integer currentHour;
  private FlightType flightType;
  private FlightStatusView flightStatusView;

  private Call<FlightResponse> arrivalCall;
  private Call<FlightResponse> departureCall;
  private boolean lastKnownAsVisible = true;

  public void setTerminalId(TerminalId terminalId){
    this.terminalId = terminalId;
  }

  public void setCurrentHour(Integer currentHour){
    this.currentHour = currentHour;
  }

  public void setFlightType(FlightType flightType){
    this.flightType = flightType;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    flightStatusView = (FlightStatusView)inflater.inflate(R.layout.fragment_flight_status,
        container, false);
    return flightStatusView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    flightStatusView.setStatusText(getString(this.terminalId.titleStringRes()) + " " + getString(this
        .flightType.titleStringRes()));
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      flightStatusView.startTimer(new TimerListener() {
        @Override
        public void callback() {
          updateFlightTable();
        }
      });
    } else if (flightStatusView.timerView != null) {
      flightStatusView.stopTimer();
    }
    lastKnownAsVisible = isVisibleToUser;
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onResume() {
    super.onResume();

    if (lastKnownAsVisible) {
      flightStatusView.startTimer(new TimerListener() {
        @Override
        public void callback() {
          updateFlightTable();
        }
      });
    }

    flightStatusView.setReachabilityVisibility(!Util.internetConnected(getContext()));
  }

  @Override
  public void onPause() {
    super.onPause();

    if (flightStatusView.timerView != null) {
      flightStatusView.stopTimer();
    }
    if (arrivalCall != null) {
      arrivalCall.cancel();
    }
    if (departureCall != null) {
      departureCall.cancel();
    }
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  private void updateFlightTable() {
    flightStatusView.toggleLoading(true);

    Callback<FlightResponse> callback = new Callback<FlightResponse>() {
      @Override
      public void onResponse(Call<FlightResponse> call, Response<FlightResponse> response) {
        if (getContext() != null) {
          flightStatusView.toggleLoading(false);
          if (response.body() != null
              && response.body().response != null
              && response.body().response.details != null) {
            flightStatusView.reloadData(response.body().response.details);
          }
        }
      }

      @Override
      public void onFailure(Call<FlightResponse> call, Throwable t) {
        if (getContext() != null) {
          flightStatusView.toggleLoading(false);
          flightStatusView.reloadData(null);
        }
      }
    };

    if (flightType == FlightType.ARRIVALS) {
      arrivalCall = SfoApi.getInstance()
          .requestArrivalFlightsForTerminal(terminalId.toInt(), currentHour);
      arrivalCall.clone().enqueue(callback);
    } else if (flightType == FlightType.DEPARTURES) {
      departureCall = SfoApi.getInstance()
          .requestDepartureFlightsForTerminal(terminalId.toInt(), currentHour);
      departureCall.clone().enqueue(callback);
    }
  }

  @Subscribe
  public void reachabilityChanged(ReachabilityEvent event) {
    flightStatusView.setReachabilityVisibility(!event.internetReachable);
  }
}
