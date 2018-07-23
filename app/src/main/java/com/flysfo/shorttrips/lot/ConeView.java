package com.flysfo.shorttrips.lot;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ConeView extends LinearLayout {

  @BindView(R.id.img_cone)
  ImageView coneImageView;

  @BindView(R.id.text_last_updated)
  TextView lastUpdatedTextView;

  public ConeView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public ConeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public ConeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ConeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_cone, this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
    Picasso.with(getContext()).load(R.drawable.cone).into(coneImageView);
  }

  void setLastUpdated(String lastUpdated) {
    lastUpdatedTextView.setText(lastUpdated);
  }
}
