package com.flysfo.shorttrips.lot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.PushReceivedEvent;
import com.flysfo.shorttrips.events.ReachabilityEvent;
import com.flysfo.shorttrips.model.dispatcher.Cone;
import com.flysfo.shorttrips.model.dispatcher.ConeResponse;
import com.flysfo.shorttrips.model.queue.QueueLengthResponse;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.refresh.TimerListener;
import com.flysfo.shorttrips.refresh.TimerView;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mattluedke on 1/4/16.
 */
public class LotFragment extends Fragment {

  @BindView(R.id.reachability)
  FrameLayout reachabilityView;

  @BindView(R.id.timer_view)
  TimerView timerView;

  @BindView(R.id.lotview)
  LotView lotView;

  @BindView(R.id.coneview)
  ConeView coneView;

  @BindView(R.id.lot_loading_spinner)
  ProgressBar lotLoadingSpinner;

  private int loadingStatus = 0;
  private Call<QueueLengthResponse> queueCall;
  private Call<ConeResponse> coneCall;
  private boolean lastKnownAsVisible = false;
  private Boolean needsToRunUserHintCode = null;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_lot, container, false);
    ButterKnife.bind(this, view);

    if (needsToRunUserHintCode != null) {
      userHintCode(needsToRunUserHintCode);
    }

    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isResumed()) { // added to avoid NPE on timerView
      userHintCode(isVisibleToUser);
    } else {
      needsToRunUserHintCode = isVisibleToUser;
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override
  public void onResume() {
    super.onResume();

    if (lastKnownAsVisible) {
      timerView.start(new TimerListener() {
        @Override
        public void callback() {
          refreshLotStatus();
        }
      });
    }

    reachabilityView.setVisibility(
        Util.internetConnected(getContext())
            ? View.GONE
            : View.VISIBLE
    );
  }

  @Override
  public void onPause() {
    super.onPause();

    if (timerView != null) {
      timerView.stop();
    }
    if (queueCall != null) {
      queueCall.cancel();
    }
    if (coneCall != null) {
      coneCall.cancel();
    }
  }

  private void userHintCode(boolean isVisibleToUser) {
    if (isVisibleToUser) {
      timerView.start(new TimerListener() {
        @Override
        public void callback() {
          refreshLotStatus();
        }
      });
    } else if (timerView != null) {
      timerView.stop();
    }
    lastKnownAsVisible = isVisibleToUser;
    needsToRunUserHintCode = null;
  }

  private void refreshLotStatus() {

    toggleLoading(2);
    queueCall = SfoApi.getInstance().requestQueueLength();
    coneCall = SfoApi.getInstance().fetchCone();

    queueCall.clone().enqueue(new Callback<QueueLengthResponse>() {
      @Override
      public void onResponse(Call<QueueLengthResponse> call, Response<QueueLengthResponse>
          response) {
        if (response.body() != null
            && response.body().response != null
            && response.body().response.getLongQueueLength() != null
            && getContext() != null) {

          lotView.setTaxiText(
              String.format(
                  getContext().getString(R.string.number),
                  response.body().response.getLongQueueLength()
              )
          );
          toggleLoading(-1);
        }
      }

      @Override
      public void onFailure(Call<QueueLengthResponse> call, Throwable t) {
        toggleLoading(-1);
        t.printStackTrace();
      }
    });

    coneCall.clone().enqueue(new Callback<ConeResponse>() {
      @Override
      public void onResponse(Call<ConeResponse> call, Response<ConeResponse> response) {
        toggleLoading(-1);
        if (response.body() != null
            && response.body().response != null) {

          Cone cone = response.body().response;
          coneView.setLastUpdated(cone.lastUpdatedString());
          coneView.setVisibility(cone.isConed ? View.VISIBLE : View.GONE);
        }
      }

      @Override
      public void onFailure(Call<ConeResponse> call, Throwable t) {
        toggleLoading(-1);
        t.printStackTrace();
      }
    });
  }

  private void toggleLoading(int changeToLoadingStatus) {
    loadingStatus += changeToLoadingStatus;
    boolean loading = loadingStatus > 0;

    lotView.setLoading(loading);
    lotLoadingSpinner.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  @Subscribe
  public void pushReceived(PushReceivedEvent event) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        timerView.updateAndRefresh();
      }
    });
  }

  @Subscribe
  public void reachabilityChanged(ReachabilityEvent event) {
    reachabilityView.setVisibility(
        event.internetReachable
            ? View.GONE
            : View.VISIBLE
    );
  }
}
