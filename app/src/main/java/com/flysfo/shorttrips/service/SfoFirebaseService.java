package com.flysfo.shorttrips.service;

import android.util.Log;

import com.flysfo.shorttrips.events.PushReceivedEvent;
import com.flysfo.shorttrips.util.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mattluedke on 11/29/16.
 */

public class SfoFirebaseService extends FirebaseMessagingService {
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);

    if (remoteMessage.getNotification() != null) {
      EventBus.getDefault().post(new PushReceivedEvent(remoteMessage));
    }

    Util.setKnownId(remoteMessage.getMessageId());
  }
}
