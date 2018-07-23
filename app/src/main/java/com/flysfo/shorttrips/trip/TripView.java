package com.flysfo.shorttrips.trip;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.util.Speaker;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mattluedke on 3/2/16.
 */
public class TripView extends FrameLayout {

  private static final long ANIMATION_DELAY = 5000;
  private static final long ANIMATION_DURATION = 500;

  @BindView(R.id.text_title)
  TextView titleTextView;

  @BindView(R.id.img_icon)
  ImageView iconImageView;

  @BindView(R.id.countdown)
  CountdownView countdownView;

  @BindView(R.id.notification_view)
  NotificationView notificationView;

  @BindView(R.id.reachability)
  FrameLayout reachabilityView;

  private StatePrompt currentPrompt;

  public TripView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public TripView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public TripView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TripView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_trip, this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  void notifyFailure(ValidationStep validationStep) {
    SfoPreferences.setPendingFailure(getContext(), validationStep);
    final int fullHeight = this.getMeasuredHeight();
    notificationView.getLayoutParams().height = fullHeight;
    notificationView.requestLayout();

    notificationView.notifyFail(validationStep, ANIMATION_DELAY, ANIMATION_DURATION);
    changeNotificationVisibility(true);

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {

      @Override
      public void run() {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0.15f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            notificationView.getLayoutParams().height = (int) (fullHeight * value);
            notificationView.requestLayout();
          }
        });
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.start();
      }

    }, ANIMATION_DELAY);
  }

  void notifySuccess(String date) {
    notificationView.getLayoutParams().height = this.getMeasuredHeight();
    notificationView.requestLayout();
    notificationView.notifySuccess(date);
    changeNotificationVisibility(true);
    SfoPreferences.setPendingSuccess(getContext(), date);
  }

  void changeNotificationVisibility(boolean visible) {
    notificationView.setVisibility(visible ? VISIBLE : GONE);
    changeCountdownVisibility(false);
    if (!visible) {
      SfoPreferences.setPendingSuccess(getContext(), null);
      SfoPreferences.setPendingFailure(getContext(), ValidationStep.UNSPECIFIED);
    }
  }

  void changeCountdownVisibility(boolean visible) {

    if (visible) {
      countdownView.setVisibility(VISIBLE);

    } else if (notificationView.getVisibility() == VISIBLE) {
      countdownView.setVisibility(INVISIBLE);

    } else {
      countdownView.setVisibility(GONE);
    }
  }

  void updateCountdown(Long elapsedTime) {
    countdownView.updateCountdown(elapsedTime);
  }

  void updatePrompt(StatePrompt statePrompt) {
    if (statePrompt != currentPrompt) {
      titleTextView.setText(statePrompt.visualStringRes());
      Picasso.with(getContext()).load(statePrompt.imageRes()).into(iconImageView);
      Speaker.getInstance().speak(statePrompt.audioStringRes());
      currentPrompt = statePrompt;
    }
  }

  public void setReachabilityVisibility(boolean visible) {
    reachabilityView.setVisibility(visible ? VISIBLE : GONE);
  }
}
