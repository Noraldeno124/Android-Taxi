package com.flysfo.shorttrips.flight;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.terminal.TerminalId;
import com.flysfo.shorttrips.model.terminal.TerminalSummary;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mattluedke on 3/7/16.
 */
public class TerminalView extends LinearLayout {

  @BindView(R.id.text_terminal_name)
  TextView terminalNameTextView;

  @BindView(R.id.text_on_time)
  TextView onTimeTextView;

  @BindView(R.id.text_delayed)
  TextView delayedTextView;

  @BindView(R.id.img_chevron)
  ImageView chevronImg;

  @BindView(R.id.key_ontime)
  LinearLayout onTimeKeyLayout;

  @BindView(R.id.key_delayed)
  LinearLayout delayedKeyLayout;

  private TerminalId terminalId;

  public TerminalView(Context context) {
    super(context);
    setup(context);
    onFinishInflate();
  }

  public TerminalView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setup(context);
  }

  public TerminalView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setup(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TerminalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setup(context);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  private void setup(final Context context) {
    LayoutInflater.from(context).inflate(R.layout.view_terminal, this);
  }

  void clear() {
    onTimeTextView.setText("");
    delayedTextView.setText("");
  }

  public void transformAsFooter() {
    terminalNameTextView.setTypeface(null, Typeface.BOLD);
    chevronImg.setVisibility(View.INVISIBLE);
    terminalNameTextView.setText(R.string.total);
  }

  public void transformAsHeader(){
    transformAsFooter();
    terminalNameTextView.setText(R.string.terminals);
    onTimeTextView.setVisibility(View.GONE);
    delayedTextView.setVisibility(View.GONE);
    onTimeKeyLayout.setVisibility(View.VISIBLE);
    delayedKeyLayout.setVisibility(View.VISIBLE);
  }

  void configureForTerminalSummary(TerminalSummary summary) {
    this.terminalId = summary.getTerminalId();
    terminalNameTextView.setText(summary.getTerminalId().titleStringRes());
    onTimeTextView.setText(
        String.format(getContext().getString(R.string.number), summary.onTimeCount)
    );
    delayedTextView.setText(
        String.format(getContext().getString(R.string.number), summary.delayedCount)
    );
  }

  public TerminalId getTerminalId() {
    return this.terminalId;
  }
}
