package com.flysfo.shorttrips.trip;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mattluedke on 3/2/16.
 */
public class CountdownView extends LinearLayout {

  @BindView(R.id.text_countdown)
  TextView countdownTextView;

  public CountdownView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public CountdownView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CountdownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_countdown, this);
  }

  void updateCountdown(Long elapsedTime) {
    if (elapsedTime == null) {
      throw new RuntimeException("shouldn't be updating countdown with null time");
    } else {
      Long remainingTime = (TripManager.TRIP_LENGTH_LIMIT - elapsedTime) / 1000;
      Long remainingHours = remainingTime / (60 * 60);
      Long remainingMinutes = (remainingTime - remainingHours * 60 * 60) / 60;
      Long remainingSeconds = remainingTime - remainingHours * 60 * 60 - remainingMinutes * 60;

      countdownTextView.setText(
          String.format(
              getResources().getString(R.string.countdown_format),
              remainingHours,
              remainingMinutes,
              remainingSeconds
          )
      );
    }
  }
}
