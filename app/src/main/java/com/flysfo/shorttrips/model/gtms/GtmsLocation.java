package com.flysfo.shorttrips.model.gtms;

/**
 * Created by mattluedke on 2/8/16.
 */
public enum  GtmsLocation {
  DT_ENTRANCE,
  DOM_EXIT,
  INTL_ARRIVAL_EXIT,
  COURTYARD_G,
  INTL_DEPARTURE_EXIT,
  COURTYARD_A,
  ITA_ENTRANCE,
  ITD_ENTRANCE,
  DTA_ENTRANCE,
  TAXI_BAIL_OUT,
  DTD_ENTRANCE,
  DTA_RECIRCULATION,
  DTA_EXIT,
  DTD_RECIRCULATION,
  TAXI_MAIN_LOT,
  NON_DISPATCHED_TAXI_EXIT,
  TAXI_STATUS,
  LOT_CC,
  DOMESTIC_ARRIVAL_TERMINAL_1,
  DOMESTIC_ARRIVAL_TERMINAL_2,
  TAXI_ENTRY,
  GTU_AREA,
  RENTAL_CAR;

  public static GtmsLocation fromCidId(String cidId) {
    switch (cidId) {
      case "CID11":
      case "CID12":
      case "CID13":
      case "CID14":
          return TAXI_ENTRY;
      case "CID21":
      case "CID22":
      case "CID23":
        return TAXI_MAIN_LOT;
      case "CID31":
      case "CID32":
        return NON_DISPATCHED_TAXI_EXIT;
      case "CID41":
        return TAXI_STATUS;
      default:
        return null;
    }
  }

  public static GtmsLocation fromAviId(String aviId) {
    switch (aviId) {
      case "L1AVI1":
      case "L1AVI2":
      case "L1AVI3":
        return DT_ENTRANCE;
      case "L2AVI1":
      case "L2AVI2":
      case "L2AVI3":
        return DOM_EXIT;
      case "L3AVI1":
        return INTL_ARRIVAL_EXIT;
      case "L4AVI1":
      case "L4AVI2":
        return COURTYARD_G;
      case "L5AVI1":
      case "L5AVI2":
        return INTL_DEPARTURE_EXIT;
      case "L6AVI1":
        return COURTYARD_A;
      case "L7AVI1":
      case "L7AVI2":
      case "L7AVI3":
        return ITA_ENTRANCE;
      case "L8AVI1":
      case "L8AVI2":
      case "L8AVI3":
      case "L8AVI4":
      case "L8AVI5":
        return ITD_ENTRANCE;
      case "L9AVI1":
      case "L9AVI2":
      case "L9AVI3":
      case "L9AVI4":
        return DTA_ENTRANCE;
      case "L10AVI1":
        return TAXI_BAIL_OUT;
      case "L11AVI1":
      case "L11AVI2":
      case "L11AVI3":
      case "L11AVI4":
        return DTD_ENTRANCE;
      case "L12AVI1":
        return DTA_RECIRCULATION;
      case "L13AVI1":
        return DTA_EXIT;
      case "L14AVI1":
      case "L14AVI2":
        return DTD_RECIRCULATION;
      case "L15AVI1":
      case "L15AVI2":
      case "L15AVI3":
        return TAXI_MAIN_LOT;
      case "L16AVI1":
        return NON_DISPATCHED_TAXI_EXIT;
      case "L17AVI1":
        return TAXI_STATUS;
      case "L18AVI1":
        return LOT_CC;
      case "L19AVI1":
      case "L19AVI2":
        return DOMESTIC_ARRIVAL_TERMINAL_1;
      case "L20AVI1":
      case "L20AVI2":
        return DOMESTIC_ARRIVAL_TERMINAL_2;
      case "L27AVI1":
      case "L27AVI2":
        return TAXI_ENTRY;
      case "L33AVI1":
        return GTU_AREA;
      case "L35AVI1":
        return RENTAL_CAR;

      default:
        return null;
    }
  }
}
