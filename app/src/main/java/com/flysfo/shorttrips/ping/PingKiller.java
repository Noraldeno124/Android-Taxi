package com.flysfo.shorttrips.ping;

import android.os.Handler;

import com.flysfo.shorttrips.events.PingKillerActive;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mattluedke on 2/16/16.
 */
public class PingKiller {
  private static PingKiller instance = null;
  private PingKiller() { }
  public static PingKiller getInstance() {
    if (instance == null) {
      instance = new PingKiller();
    }
    return instance;
  }

  private Handler timerHandler = new Handler();
  private static final long timeInterval = 2 * 60 * 1000; // 2 min
  private Runnable timerRunnable = new Runnable() {

    @Override
    public void run() {
      turnPingsBackOn();
    }
  };
  private Boolean killPings = false;

  private void turnPingsBackOn() {
    killPings = false;
  }

  public void turnPingsOffForAWhile() {
    killPings = true;
    timerHandler.postDelayed(timerRunnable, timeInterval);
    EventBus.getDefault().post(new PingKillerActive());
  }

  public Boolean shouldKillPings() {
    return killPings;
  }
}
