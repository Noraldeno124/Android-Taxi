package com.flysfo.shorttrips.debug;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.geofence.SfoGeofence;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("SetTextI18n")
public class DebugView extends FrameLayout {

  private Integer gtmsCount = 0;

  @BindView(R.id.text_geofences)
  TextView geofenceTextView;

  @BindView(R.id.text_location)
  TextView locationTextView;

  @BindView(R.id.text_state)
  TextView stateTextView;

  @BindView(R.id.text_gtms_calls)
  TextView gtmsCallsTextView;

  @BindView(R.id.text_cid)
  TextView cidTextView;

  @BindView(R.id.text_avi)
  TextView aviTextView;

  @BindView(R.id.text_console)
  TextView consoleTextView;

  @BindView(R.id.btn_debug1)
  Button debugButton1;

  @BindView(R.id.btn_debug2)
  Button debugButton2;

  @BindView(R.id.btn_debug3)
  Button debugButton3;

  @BindView(R.id.reachability)
  FrameLayout reachabilityView;

  public DebugView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public DebugView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public DebugView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DebugView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_debug, this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
    consoleTextView.setMovementMethod(new ScrollingMovementMethod());
  }

  void incrementGtms() {
    gtmsCount += 1;
    gtmsCallsTextView.setText("GTMS calls: " + gtmsCount);
  }

  void updateGeofenceList(List<SfoGeofence> geofences) {
    String text = "Last known geofences: ";

    if (geofences != null) {
      for (SfoGeofence geofence : geofences) {
        text += "" + geofence.name() + "\n";
      }
    }

    geofenceTextView.setText(text);
  }

  void updateAvi(String avi) {
    aviTextView.setText("Last AVI: " + avi);
  }

  void updateLocation(Location location) {
    if (location != null) {
      locationTextView.setText("Location: "
          + location.getLatitude()
          + ", "
          + location.getLongitude());
    }
  }

  void updateState(String stateName) {
    stateTextView.setText("State: " + stateName);
  }

  void updateCid(String cidString) {
    cidTextView.setText("Last CID: " + cidString);
  }

  void printDebugLine(String text) {
    printDebugLine(text, DebugType.NEUTRAL);
  }

  void printDebugLine(String text, DebugType debugType) {

    Log.d("SHORT TRIPS", text);

    int start = consoleTextView.getText().length();
    consoleTextView.append("\n" + text);
    int end = consoleTextView.getText().length();

    Spannable spannableText = (Spannable) consoleTextView.getText();
    spannableText.setSpan(new ForegroundColorSpan(debugType.color()), start, end, 0);
    consoleTextView.scrollTo(0, consoleTextView.getHeight());
  }

  void updateFakeButtons(String title1, OnClickListener action1) {

    updateFakeButtons(title1, action1, null, null, null, null);
  }

  void updateFakeButtons(String title1, OnClickListener action1, String title2,
                         OnClickListener action2) {

    updateFakeButtons(title1, action1, title2, action2, null, null);
  }

  void updateFakeButtons(String title1, OnClickListener action1, String title2,
                         OnClickListener action2, String title3, OnClickListener action3) {

    updateButton(debugButton1, title1, action1);
    updateButton(debugButton2, title2, action2);
    updateButton(debugButton3, title3, action3);
  }

  private void updateButton(Button button, String title, OnClickListener action) {
    button.setText(title);
    button.setOnClickListener(action);
  }

  public void setReachabilityVisibility(boolean visible) {
    reachabilityView.setVisibility(visible ? VISIBLE : GONE);
  }
}
