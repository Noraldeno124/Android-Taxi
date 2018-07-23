package com.flysfo.shorttrips.trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mattluedke on 2/11/16.
 */
public class TripDateTransform {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; // 2015-09-15T16:13:48Z

  public static String fromDate(Date date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    return dateFormat.format(date);
  }

  static String fromNow() {
    return fromDate(new Date());
  }
}
