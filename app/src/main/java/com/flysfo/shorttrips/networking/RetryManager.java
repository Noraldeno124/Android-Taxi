package com.flysfo.shorttrips.networking;

/**
 * Created by mattluedke on 1/5/17.
 */

public class RetryManager {

  public static final int MAX_RETRIES = 10;

  public static long timeInterval(int retryCount) {
    return (retryCount + 1) * 5 * 1000;
  }
}
