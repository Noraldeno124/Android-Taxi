package com.flysfo.shorttrips.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.ReachabilityEvent;
import com.flysfo.shorttrips.util.Speaker;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;

public class ReachabilityReceiver extends BroadcastReceiver {

  private static boolean lastAnnouncedAsConnected = true;
  private static final long timeInterval = 15 * 1000; // 15sec
  private static Handler timerHandler = new Handler();

  @Override
  public void onReceive(Context context, Intent intent) {

    final boolean connected = Util.internetConnected(context);

    EventBus.getDefault().post(new ReachabilityEvent(connected));

    if (lastAnnouncedAsConnected == connected) {
      // cancel any pending notification
      timerHandler.removeCallbacksAndMessages(null);

    } else {
      // schedule notification in 15 seconds
      timerHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          Speaker.getInstance().speak(
              connected
                  ? R.string.internet_reestablished
                  : R.string.internet_lost
          );
          lastAnnouncedAsConnected = connected;
        }
      }, timeInterval);
    }
  }
}
