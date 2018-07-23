package com.flysfo.shorttrips.model.dispatcher;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cone implements Serializable {

  private static final String DATE_FORMAT = "hh:mm a"; // "2:50 PM"

  public boolean isConed;
  public Date lastUpdated;

  public String lastUpdatedString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US); // "2:50 PM"
    return dateFormat.format(lastUpdated);
  }
}
