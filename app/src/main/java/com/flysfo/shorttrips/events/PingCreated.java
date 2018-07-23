package com.flysfo.shorttrips.events;

import com.flysfo.shorttrips.model.ping.Ping;

/**
 * Created by mattluedke on 2/16/16.
 */
public class PingCreated {
  public final Ping ping;

  public PingCreated(Ping ping) {
    this.ping = ping;
  }
}
