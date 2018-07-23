package com.flysfo.shorttrips.debug;

import android.graphics.Color;

public enum DebugType {
  POSITIVE,
  NEUTRAL,
  NEGATIVE;

  public int color() {
    switch (this) {
      case POSITIVE:
        return Color.GREEN;
      case NEGATIVE:
        return Color.RED;
      default:
        return Color.BLACK;
    }
  }
}
