package com.flysfo.shorttrips.ping;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Util;

/**
 * Created by mattluedke on 3/22/16.
 */
public class PingReceiver extends WakefulBroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (Util.isAirplaneModeOn(context)) {
      TripManager.getInstance().invalidate(ValidationStep.NETWORK_FAILURE);
    } else {
      PingManager.getInstance(context).sendNewPing();
      PingManager.getInstance(context).sendOldPings();
      PingManager.getInstance(context).scheduleAlarm();
    }
  }
}
