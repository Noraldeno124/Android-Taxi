package com.flysfo.shorttrips.networking;

public class Url {

  static final String base = "https://api-stage.flysfo.com/taxi_ws/services/"; // staging
//  static final String base = "https://api.flysfo.com/taxi_ws/services/"; // production
  
  public static boolean isStaging() {
    return base.compareTo("https://api-stage.flysfo.com/taxi_ws/services/") == 0;
  }

  static final String securities = "securities/";

  public static final class Taxi {

    private static final String taxi = "taxi/";

    public static final class Airline {

      private static final String airline = base + taxi + "airline/";

      public static String logoPng(String iataCode) {
        return airline + "logo/" + iataCode;
      }
    }

    static final class Device {
      private static final String device = taxi + "device/";

      static final String stateIdPath = "stateId";

      static final String mobileStateUpdate = device + "mobile/state/" + "{" + stateIdPath + "}" + "/update";

      static final class Avi {
        private static final String avi = device + "avi";

        static final String transponderIdPath = "transponderId";

        static final String transponder = avi + "/transponder/" + "{" + transponderIdPath + "}";
      }

      static final class Cid {
        private static final String cid = device + "cid";

        static final String driverIdPath = "driverId";

        static final String driver = cid + "/driver/" + "{" + driverIdPath + "}";
      }
    }

    static final class Dispatcher {
      private static final String dispatcher = taxi + "dispatcher/";

      static final String cone = dispatcher + "cone";
    }

    static final class Driver {
      private static final String driver = taxi + "driver/";

      static final String login = driver + "login";

      static final String smartCardPath = "smartCard";

      static final String vehicle = driver + "vehicle/smart_card/" + "{" + smartCardPath + "}";
    }

    static final class Flight {
      private static final String flight = taxi + "flight/";

      static final class Arrival {
        private static final String arrival = flight + "arrival/";

        static final String summary = arrival + "summary";
        static final String details = arrival + "details";
      }

      static final class Departure {
        private static final String departure = flight + "departure/";

        static final String summary = departure + "summary";
        static final String details = departure + "details";
      }
    }

    static final class Lot {
      private static final String lot = taxi + "lot/";

      static final String lotCounter = lot + "lot_counter";
      static final String eventType = lot + "event_type";

      static final String currentTransaction = lot + "current_transaction";

      static final String driverCardId = "driverCardId";
      static final String currentTransactionWithID = lot + "current_transaction/" + "{" +
          driverCardId + "}";

      static final String transactionLogId = "transactionLogId";
      static final String transactionLog = lot + "transaction_log/" + "{" + transactionLogId + "}";
    }

    static final class Queue {
      static final String currentLength = taxi + "queue/current_size";
    }

    static final class Reference {
      private static final String reference = taxi + "reference/";

      static final String platformParam = "platform";

      static final String clientVersion = reference + "client_version";
      static final String lotCapacity = reference + "lot_capacity";
      static final String terms = reference + "terms";
    }

    static final class Trip {
      private static final String trip = taxi + "trip/";

      static final String tripIdPath = "tripId";

      static final String end = trip + "{" + tripIdPath + "}" + "/end";
      static final String invalidate = trip + "{" + tripIdPath + "}" + "/invalidate";
      static final String ping = trip + "{" + tripIdPath + "}/ping";
      static final String pings = trip + "{" + tripIdPath + "}/delayed_pings";
      static final String start = trip + "start";
    }
  }
}
