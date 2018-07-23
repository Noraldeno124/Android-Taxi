package com.flysfo.shorttrips.trip;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.util.Speaker;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mattluedke on 3/2/16.
 */
public class NotificationView extends LinearLayout {

  private static final int FULL_TITLE_TEXT_SIZE_SP = 26;
  private static final int SMALL_TITLE_TEXT_SIZE_SP = 15;

  @BindView(R.id.text_title)
  TextView titleTextView;

  @BindView(R.id.img_icon)
  ImageView iconImageView;

  @BindView(R.id.space)
  Space space;

  public NotificationView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public NotificationView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public NotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public NotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_notification, this);
  }

  void notifySuccess(String time) {
    ((LayoutParams) iconImageView.getLayoutParams()).weight = 2;
    ((LayoutParams) space.getLayoutParams()).weight = 1;
    titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FULL_TITLE_TEXT_SIZE_SP);
    iconImageView.requestLayout();
    space.requestLayout();

    setBackgroundColor(getResources().getColor(R.color.notification_green));
    titleTextView.setText(
        String.format(
            getContext().getString(R.string.valid_short_trip),
            time
        )
    );

    iconImageView.setImageResource(R.drawable.check);
    Speaker.getInstance().speak(R.string.the_trip_has_ended_and_was_recorded_as_a_valid_short_trip);
  }

  void notifyFail(ValidationStep validationStep, long delay, final long duration) {

    ((LayoutParams) iconImageView.getLayoutParams()).weight = 2;
    ((LayoutParams) space.getLayoutParams()).weight = 1;
    titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FULL_TITLE_TEXT_SIZE_SP);
    iconImageView.requestLayout();
    space.requestLayout();

    setBackgroundColor(getResources().getColor(R.color.notification_red));
    titleTextView.setText(validationStep.visualStringRes());
    iconImageView.setImageResource(R.drawable.exclamation);
    Speaker.getInstance().speak(validationStep.audioStringRes());

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {

      @Override
      public void run() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override
          public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            ((LayoutParams) iconImageView.getLayoutParams()).weight = 2 * value;
            ((LayoutParams) space.getLayoutParams()).weight = value;
            titleTextView.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                SMALL_TITLE_TEXT_SIZE_SP + (FULL_TITLE_TEXT_SIZE_SP - SMALL_TITLE_TEXT_SIZE_SP) * value
            );
            iconImageView.requestLayout();
            space.requestLayout();
          }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
      }

    }, delay);
  }
}
