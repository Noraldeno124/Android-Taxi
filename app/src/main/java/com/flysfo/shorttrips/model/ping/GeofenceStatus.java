package com.flysfo.shorttrips.model.ping;

import java.io.Serializable;

/**
 * Created by mattluedke on 2/16/16.
 */
public enum GeofenceStatus implements Serializable {
  NOT_VERIFIED,
  INVALID,
  VALID;

  public static GeofenceStatus fromInt(Integer integer) {
    if (integer == 0) {
      return INVALID;
    } else if (integer == 1) {
      return VALID;
    } else {
      return NOT_VERIFIED;
    }
  }

  public Integer toInt() {
    switch (this) {
      case INVALID:
        return 0;
      case VALID:
        return 1;
      default:
        return -1;
    }
  }

  public Boolean toBool() {
    return this == VALID;
  }

  public static GeofenceStatus fromBoolean(Boolean bool) {
    return bool ? VALID : INVALID;
  }
}
