package com.flysfo.shorttrips.model.terminal;

import com.flysfo.shorttrips.R;

/**
 * Created by mattluedke on 3/7/16.
 */
public enum TerminalId {
  ONE,
  TWO,
  THREE,
  INTERNATIONAL;

  static TerminalId fromInt(int value) {
    if (value == 1) {
      return ONE;
    } else if (value == 2) {
      return TWO;
    } else if (value == 3) {
      return THREE;
    } else if (value == 4) {
      return INTERNATIONAL;
    } else {
      throw new RuntimeException("bad enum");
    }
  }

  public int toInt() {
    switch (this) {
      case ONE:
        return 1;
      case TWO:
        return 2;
      case THREE:
        return 3;
      case INTERNATIONAL:
        return 4;
      default:
        throw new RuntimeException("bad enum");
    }
  }


  public int titleStringRes() {
    switch (this) {
      case ONE:
        return R.string.terminal_one;
      case TWO:
        return R.string.terminal_two;
      case THREE:
        return R.string.terminal_three;
      case INTERNATIONAL:
        return R.string.terminal_intl;
      default:
        throw new RuntimeException("bad enum");
    }
  }
}
