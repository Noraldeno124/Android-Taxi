package com.flysfo.shorttrips.events;

import com.google.firebase.messaging.RemoteMessage;

public class PushReceivedEvent {
  public final String title;
  public final String body;

  public PushReceivedEvent(RemoteMessage message) {
    this.title = message.getNotification().getTitle();
    this.body = message.getNotification().getBody();
  }
}
