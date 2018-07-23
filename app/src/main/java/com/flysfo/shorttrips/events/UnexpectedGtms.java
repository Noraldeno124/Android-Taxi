package com.flysfo.shorttrips.events;

import android.annotation.SuppressLint;

@SuppressLint("SetTextI18n")
public class UnexpectedGtms {
  public final String[] expecteds;
  public final String found;

  public UnexpectedGtms(String[] expecteds, String found) {
    this.expecteds = expecteds;
    this.found = found;
  }

  public String message() {
    String message =  "Unexpected GTMS. Expected:";

    for (String expected : expecteds) {
      message += " " + expected;
    }

    message += ", Found: " + found;

    return message;
  }
}
