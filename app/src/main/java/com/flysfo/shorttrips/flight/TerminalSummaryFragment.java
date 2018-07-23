package com.flysfo.shorttrips.flight;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.events.ReachabilityEvent;
import com.flysfo.shorttrips.model.flight.FlightType;
import com.flysfo.shorttrips.model.terminal.TerminalSummaryResponse;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.refresh.TimerListener;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 1/4/16.
 */
public class TerminalSummaryFragment extends Fragment {

  private TerminalSummaryView terminalSummaryView;
  private OnTerminalSelectedListener onTerminalSelectedListener;
  private Call<TerminalSummaryResponse> call;
  private boolean lastKnownAsVisible = false;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    terminalSummaryView = (TerminalSummaryView)inflater.inflate(R.layout.fragment_terminal_summary, container, false);
    terminalSummaryView.setOnTerminalSelectedListener(onTerminalSelectedListener);
    return terminalSummaryView;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    terminalSummaryView.setOnControlChangedListener(new OnControlChangedListener() {
      @Override
      public void onControlChanged() {
        updateTerminalData();
      }
    });
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      terminalSummaryView.startTimer(new TimerListener() {
        @Override
        public void callback() {
          updateTerminalData();
        }
      });
    } else if (terminalSummaryView.timerView != null) {
      terminalSummaryView.stopTimer();
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
      terminalSummaryView.startTimer(new TimerListener() {
        @Override
        public void callback() {
          updateTerminalData();
        }
      });
    }

    terminalSummaryView.setReachabilityVisibility(!Util.internetConnected(getContext()));
  }

  @Override
  public void onPause() {
    super.onPause();

    if (terminalSummaryView.timerView != null) {
      terminalSummaryView.stopTimer();
    }

    if (call != null) {
      call.cancel();
    }
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  public void setOnTerminalSelectedListener(OnTerminalSelectedListener onTerminalSelectedListener){
    this.onTerminalSelectedListener = onTerminalSelectedListener;
  }

  private void updateTerminalData() {

    terminalSummaryView.clearTerminalViews();
    terminalSummaryView.timerView.resetProgress();
    terminalSummaryView.toggleProgress(true);

    final FlightType requestFlightType = terminalSummaryView.getCurrentFlightType();
    final int requestHour = terminalSummaryView.getCurrentHour();

    switch (requestFlightType) {
      case ARRIVALS:
        call = SfoApi.getInstance().requestArrivalTerminalSummaries(requestHour);
        break;
      case DEPARTURES:
        call = SfoApi.getInstance().requestDepartureTerminalSummaries(requestHour);
        break;
    }

    call.clone().enqueue(new Callback<TerminalSummaryResponse>() {
      @Override
      public void onResponse(Call<TerminalSummaryResponse> call, Response<TerminalSummaryResponse>
          response) {
        EventBus.getDefault().post(new NetworkResponse(response));

        if (response != null
            && response.body() != null
            && response.body().response != null
            && response.body().response.getActiveListWrapper() != null
            && response.body().response.getActiveListWrapper().terminalSummaries != null
            && requestFlightType == terminalSummaryView.getCurrentFlightType()
            && requestHour == terminalSummaryView.getCurrentHour()
            && getContext() != null) {

          terminalSummaryView.toggleProgress(false);
          terminalSummaryView.reloadTerminalViews(response.body().response.getActiveListWrapper()
              .terminalSummaries);
        }
      }

      @Override
      public void onFailure(Call<TerminalSummaryResponse> call, Throwable t) {
        if (getContext() != null) {
          terminalSummaryView.toggleProgress(false);
        }
        EventBus.getDefault().post(new NetworkResponse(t));
      }
    });
  }

  @Subscribe
  public void reachabilityChanged(ReachabilityEvent event) {
    terminalSummaryView.setReachabilityVisibility(!event.internetReachable);
  }
}
