package com.flysfo.shorttrips.lot;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LotView extends LinearLayout {

  @BindView(R.id.text_taxis)
  TextView taxisTextView;

  @BindView(R.id.text_taxi_caption)
  TextView taxiCaptionTextView;

  @BindView(R.id.img_circles)
  ImageView circleImageView;

  public LotView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public LotView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public LotView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_lot, this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
    Picasso.with(getContext()).load(R.drawable.bg_circles).into(circleImageView);
  }

  void setTaxiText(String text) {
    taxisTextView.setText(text);
  }

  void setLoading(boolean loading) {
    if (loading) {
      taxisTextView.setVisibility(View.GONE);
      taxiCaptionTextView.setVisibility(View.INVISIBLE);
    } else {
      taxisTextView.setVisibility(View.VISIBLE);
      taxiCaptionTextView.setVisibility(View.VISIBLE);
    }
  }
}
