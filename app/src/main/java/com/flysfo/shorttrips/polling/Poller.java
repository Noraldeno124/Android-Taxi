package com.flysfo.shorttrips.polling;

import android.os.Handler;

import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by mattluedke on 2/8/16.
 */
public abstract class Poller implements ContextHandler {

  List<Call> calls = new ArrayList<>();
  private Handler timerHandler = new Handler();
  private static final long timeInterval = 10 * 1000; // 10sec
  private Runnable timerRunnable = new Runnable() {

    @Override
    public void run() {
      action();
      timerHandler.postDelayed(this, timeInterval);
    }
  };

  @Override
  public void call(StatefulContext context) throws Exception {
    if (!Util.TESTING) {
      timerHandler.postDelayed(timerRunnable, 0);
    }
  }

  public void stop() {
    timerHandler.removeCallbacks(timerRunnable);
    for (Call call : calls) {
      call.cancel();
    }
  }

  abstract void action();
  abstract void checkForFallback();
}
