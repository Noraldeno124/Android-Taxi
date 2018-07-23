package com.flysfo.shorttrips.notification;

public enum NotificationType {
  CONE("cone"),
//  DEBUG("debug1"),
//  LOCAL_DEV("localdev")
  ;

  private final String text;

  NotificationType(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
