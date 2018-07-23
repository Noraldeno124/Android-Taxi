package com.flysfo.shorttrips.handlers;

import android.os.Handler;

import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.trip.TripManager;

/**
 * Created by mattluedke on 6/20/16.
 */
public class WaitingInHoldingLot implements ContextHandler {

  Handler timerHandler = new Handler();

  @Override
  public void call(StatefulContext context) throws Exception {
    timerHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        StateManager.getInstance().trigger(Event.INSIDE_TAXI_LOOP_EXIT);
      }
    }, TripManager.getInstance().getRightAfterValidShort()
        ? 60 * 1000
        : 10 * 60 * 1000);
  }
}
