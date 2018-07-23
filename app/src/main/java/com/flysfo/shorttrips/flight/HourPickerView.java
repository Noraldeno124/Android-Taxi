package com.flysfo.shorttrips.flight;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pierreexygy on 3/8/16.
 */
public class HourPickerView extends LinearLayout {

  private static final int DEFAULT_HOUR = 1;
  private static final int MAX_HOUR = 12;
  private static final int MIN_HOUR = -2;

  @BindView(R.id.top_text_view)
  TextView topTextView;

  @BindView(R.id.main_text_view)
  TextView mainTextView;

  @BindView(R.id.bottom_text_view)
  TextView bottomTextView;

  @BindView(R.id.btn_decrease)
  Button decreaseButton;

  @BindView(R.id.btn_increase)
  Button increaseButton;

  private Integer currentHour;
  private Integer maxHour;
  private Integer minHour;
  private OnControlChangedListener onControlChangedListener;

  public HourPickerView(Context context) {
    super(context);
    setup(context, null);
    onFinishInflate();
  }

  public HourPickerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context, attrs);
  }

  public HourPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public HourPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
    changeTime(0);
  }

  void changeTime(Integer hourChange) {
    if( (hourChange == 1 && currentHour == -1) || (hourChange == -1 && currentHour == 1) ) {
      hourChange *= 2;
    }

    Integer tempHour = currentHour + hourChange;

    if( tempHour >= minHour && tempHour <= maxHour ) {
      currentHour = tempHour;

      if( currentHour < 0 ) {
        topTextView.setText(R.string.flights);
        mainTextView.setText(
            String.format(getContext().getString(R.string.hours), Math.abs(currentHour))
        );
        bottomTextView.setText(R.string.ago);
      } else if ( currentHour > 0 ) {
        topTextView.setText(R.string.flights_in);
        mainTextView.setText(
            String.format(getContext().getString(R.string.hours), currentHour)
        );
        bottomTextView.setText("");
      }
    }

    decreaseButton.setClickable(tempHour > minHour);
    decreaseButton.setEnabled(tempHour > minHour);
    decreaseButton.setAlpha(Objects.equals(tempHour, minHour) ? .5f : 1.0f );
    increaseButton.setClickable(tempHour < maxHour );
    increaseButton.setEnabled(tempHour < maxHour);
    increaseButton.setAlpha(Objects.equals(tempHour, maxHour) ? .5f : 1.0f);
  }

  private void setup(final Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.view_hour_picker, this);

    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.HourPickerView,
        0,
        0
    );

    try {
      currentHour = a.getInteger(R.styleable.HourPickerView_default_hour, DEFAULT_HOUR);
      maxHour = a.getInteger(R.styleable.HourPickerView_max_hour, MAX_HOUR);
      minHour = a.getInteger(R.styleable.HourPickerView_min_hour, MIN_HOUR);
    } finally {
      a.recycle();
    }
  }

  Integer getCurrentHour() {
    return this.currentHour;
  }

  void setOnControlChangedListener(OnControlChangedListener listener) {
    this.onControlChangedListener = listener;
  }

  @OnClick({R.id.btn_decrease, R.id.btn_increase})
  public void hourBtnPressed(Button button) {
    Integer oldCurrentHour = currentHour;
    changeTime(button == decreaseButton ? -1 : 1);
    if (!Objects.equals(oldCurrentHour, currentHour) && onControlChangedListener != null) {
      onControlChangedListener.onControlChanged();
    }
  }
}
