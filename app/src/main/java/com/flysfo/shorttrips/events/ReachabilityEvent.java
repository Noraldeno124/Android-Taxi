package com.flysfo.shorttrips.events;

public class ReachabilityEvent {
  public final boolean internetReachable;

  public ReachabilityEvent(boolean reachable) {
    this.internetReachable = reachable;
  }
}
