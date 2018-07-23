package com.flysfo.shorttrips.flight;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.flight.FlightType;
import com.flysfo.shorttrips.model.terminal.TerminalId;
import com.flysfo.shorttrips.model.terminal.TerminalSummary;
import com.flysfo.shorttrips.refresh.TimerListener;
import com.flysfo.shorttrips.refresh.TimerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mattluedke on 3/7/16.
 */
public class TerminalSummaryView extends RelativeLayout implements AdapterView.OnItemSelectedListener {

  @BindView(R.id.terminal_view_intl)
  TerminalView terminalIntlView;

  @BindView(R.id.terminal_view_1)
  TerminalView terminalOneView;

  @BindView(R.id.terminal_view_2)
  TerminalView terminalTwoView;

  @BindView(R.id.terminal_view_3)
  TerminalView terminalThreeView;

  @BindView(R.id.terminal_total_view)
  TerminalView terminalTotalView;

  @BindView(R.id.terminal_keys_view)
  TerminalView terminalKeysView;

  @BindView(R.id.timer_view)
  TimerView timerView;

  @BindView(R.id.flight_status_spinner)
  Spinner flightStatusSpinner;

  @BindView(R.id.hour_picker_view)
  HourPickerView hourPickerView;

  @BindView(R.id.loading_spinner)
  ProgressBar loadingSpinner;

  @BindView(R.id.reachability)
  FrameLayout reachabilityView;

  private OnControlChangedListener onControlChangedListener;
  private OnTerminalSelectedListener onTerminalSelectedListener;

  public TerminalSummaryView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public TerminalSummaryView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public TerminalSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TerminalSummaryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_terminal_summary, this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);

    terminalTotalView.transformAsFooter();
    terminalKeysView.transformAsHeader();

    flightStatusSpinner.setOnItemSelectedListener(this);
    ArrayAdapter<FlightType> adapter = new ArrayAdapter<>(this.getContext(), R.layout
        .flight_status_spinner_item, FlightType.flightTypes());
    adapter.setDropDownViewResource(R.layout.flight_status_spinner_dropdown_item);
    flightStatusSpinner.setAdapter(adapter);
  }

  void startTimer(TimerListener timerListener) {
    timerView.start(timerListener);
  }

  void stopTimer() {
    timerView.stop();
  }

  FlightType getCurrentFlightType() {
    return FlightType.flightTypes()[flightStatusSpinner.getSelectedItemPosition()];
  }

  void clearTerminalViews() {
    terminalOneView.clear();
    terminalTwoView.clear();
    terminalThreeView.clear();
    terminalIntlView.clear();
    terminalTotalView.clear();
  }

  int getCurrentHour() {
    return hourPickerView.getCurrentHour();
  }

  void reloadTerminalViews(TerminalSummary[] summaries) {
    terminalOneView.configureForTerminalSummary(
        TerminalSummary.findInArray(summaries, TerminalId.ONE)
    );
    terminalTwoView.configureForTerminalSummary(
        TerminalSummary.findInArray(summaries, TerminalId.TWO)
    );
    terminalThreeView.configureForTerminalSummary(
        TerminalSummary.findInArray(summaries, TerminalId.THREE)
    );
    terminalIntlView.configureForTerminalSummary(
        TerminalSummary.findInArray(summaries, TerminalId.INTERNATIONAL)
    );

    updateTotalCount(summaries);
  }

  private void updateTotalCount(TerminalSummary[] summaries) {
    Integer total_ontime = 0;
    Integer total_delayed = 0;

    for (TerminalSummary summary : summaries) {
      total_ontime += summary.onTimeCount;
      total_delayed += summary.delayedCount;
    }

    terminalTotalView.onTimeTextView.setText(
        String.format(getContext().getString(R.string.number), total_ontime)
    );
    terminalTotalView.delayedTextView.setText(
        String.format(getContext().getString(R.string.number), total_delayed)
    );
  }


  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    if (onControlChangedListener != null) {
      onControlChangedListener.onControlChanged();
    }
  }

  public void onNothingSelected(AdapterView<?> parent) { }

  @OnClick({R.id.terminal_view_intl,
      R.id.terminal_view_1,
      R.id.terminal_view_2,
      R.id.terminal_view_3})
  public void terminalSelected(TerminalView terminal) {
    if (terminal != null
        && terminal.getTerminalId() != null
        && hourPickerView.getCurrentHour() != null
        && getCurrentFlightType() != null) {

      onTerminalSelectedListener.onTerminalSelected(terminal.getTerminalId(), hourPickerView
          .getCurrentHour(), getCurrentFlightType());
    }
  }

  public void setOnTerminalSelectedListener(OnTerminalSelectedListener onTerminalSelectedListener){
    this.onTerminalSelectedListener = onTerminalSelectedListener;
  }

  void setOnControlChangedListener(OnControlChangedListener listener) {
    this.onControlChangedListener = listener;
    hourPickerView.setOnControlChangedListener(listener);
  }

  void toggleProgress(boolean active) {
    loadingSpinner.setVisibility(active ? VISIBLE : GONE);
  }

  public void setReachabilityVisibility(boolean visible) {
    reachabilityView.setVisibility(visible ? VISIBLE : GONE);
  }
}
