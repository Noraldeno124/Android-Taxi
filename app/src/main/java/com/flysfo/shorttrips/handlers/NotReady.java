package com.flysfo.shorttrips.handlers;

import com.flysfo.shorttrips.model.trip.MobileState;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;

public class NotReady implements ContextHandler {
  @Override
  public void call(StatefulContext context) throws Exception {
    MobileState.lastKnown = MobileState.NOT_READY;
  }
}
