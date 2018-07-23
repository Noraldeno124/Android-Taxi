package com.flysfo.shorttrips.flight;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.flight.Flight;
import com.flysfo.shorttrips.refresh.TimerListener;
import com.flysfo.shorttrips.refresh.TimerView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pierreexygy on 3/9/16.
 */
public class FlightStatusView extends FrameLayout {

  @BindView(R.id.flight_status_text_view)
  TextView flightStatusTextView;

  @BindView(R.id.flight_status_list_view)
  ListView flightStatusListView;

  @BindView(R.id.flight_status_loading_layout)
  RelativeLayout flightStatusLoadingLayout;

  @BindView(R.id.timer_view)
  TimerView timerView;

  @BindView(R.id.reachability)
  FrameLayout reachabilityView;

  private FlightStatusAdapter flightStatusAdapter;

  public FlightStatusView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public FlightStatusView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public FlightStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public FlightStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
    flightStatusAdapter = new FlightStatusAdapter(getContext(), null);
    flightStatusListView.setAdapter(flightStatusAdapter);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_flight_status, this);
  }

  void startTimer(TimerListener listener) {
    timerView.start(listener);
  }

  void stopTimer() {
    timerView.stop();
  }

  public void reloadData(Flight[] flights){
    flightStatusAdapter.refreshData(flights);
  }
  public void setStatusText(String text_res){
    flightStatusTextView.setText(text_res);
  }

  public void toggleLoading(Boolean loading){
    if (loading) {
      flightStatusListView.setVisibility(View.GONE);
      flightStatusLoadingLayout.setVisibility(View.VISIBLE);
    } else {
      flightStatusListView.setVisibility(View.VISIBLE);
      flightStatusLoadingLayout.setVisibility(View.GONE);
    }
  }

  public void setReachabilityVisibility(boolean visible) {
    reachabilityView.setVisibility(visible ? VISIBLE : GONE);
  }
}
