package com.flysfo.shorttrips.refresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.util.Util;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mattluedke on 3/7/16.
 */
public class TimerView extends LinearLayout {

  private static final int DEFAULT_UPDATE_INTERVAL = 60;

  private TimerListener listener;
  private Date lastUpdate;
  private int updateInterval;

  private Handler timerHandler = new Handler();
  private static final long timeInterval = 1 * 1000; // 1sec
  private Runnable timerRunnable = new Runnable() {

    @Override
    public void run() {

      if (lastUpdate == null) {
        updateAndRefresh();
      } else {
        int elapsedSeconds = (int)((new Date().getTime() - lastUpdate.getTime()) / 1000);
        updateForTime(elapsedSeconds);

        if (elapsedSeconds >= updateInterval) {
          updateAndRefresh();
        }
      }

      timerHandler.postDelayed(this, timeInterval);
    }
  };

  @BindView(R.id.text_update)
  TextView updateTextView;

  @BindView(R.id.progress_bar)
  ProgressBar progressBar;

  public TimerView(Context context) {
    super(context);
    setup(context, null);
    onFinishInflate();
  }

  public TimerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context, attrs);
  }

  public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TimerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);

    progressBar.setMax(updateInterval);
  }

  private void setup(final Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.view_timer, this);

    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.TimerView,
        0,
        0
    );

    try {
      updateInterval = a.getInteger(R.styleable.TimerView_update_interval, DEFAULT_UPDATE_INTERVAL);
    } finally {
      a.recycle();
    }
  }

  private void updateForTime(int seconds) {
    if (seconds < 60) {
      updateTextView.setText(R.string.last_updated_less_than_a_minute_ago);
    } else if (seconds < 2 * 60) {
      updateTextView.setText(R.string.last_updated_a_minute_ago);
    } else {
      updateTextView.setText(String.format(getContext().getString(R.string
          .last_updated_x_minutes_ago), seconds / 60));
    }

    progressBar.setProgress(seconds);
  }

  public void start(TimerListener listener) {
    this.listener = listener;
    timerHandler.postDelayed(timerRunnable, timeInterval);
  }

  public void updateAndRefresh() {
    if (Util.internetConnected(getContext())) {
      resetProgress();
      if (listener != null) {
        listener.callback();
      }
    }
  }

  public void resetProgress() {
    lastUpdate = new Date();
    updateForTime(0);
  }

  public void stop() {
    timerHandler.removeCallbacks(timerRunnable);
  }
}
