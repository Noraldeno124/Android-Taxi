package com.flysfo.shorttrips.debug;

import android.location.Location;
import android.os.Handler;
import android.view.View;

import com.flysfo.shorttrips.avi.AviManager;
import com.flysfo.shorttrips.events.LocationRead;
import com.flysfo.shorttrips.events.LocationStatusUpdated;
import com.flysfo.shorttrips.events.PingCreated;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.Vehicle;
import com.flysfo.shorttrips.model.gtms.GtmsLocation;
import com.flysfo.shorttrips.model.ping.Ping;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.ping.PingKiller;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by mattluedke on 2/8/16.
 */
public class DebugActions {

  // ASSOCIATION

  static View.OnClickListener associateDriverAndVehicle = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Vehicle vehicle = new Vehicle(10590, "13702K1", "0737", 2005887, 12999);
      DriverManager.getInstance().setCurrentVehicle(vehicle);
    }
  };

  // AVI

  static View.OnClickListener entryAvi = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      AviManager.getInstance().setLatestAviLocation(GtmsLocation.TAXI_ENTRY);
      StateManager.getInstance().trigger(Event.LATEST_AVI_AT_ENTRY);
    }
  };

  static View.OnClickListener exitAvi = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      AviManager.getInstance().setLatestAviLocation(GtmsLocation.DOM_EXIT);
      StateManager.getInstance().trigger(Event.EXIT_AVI_CHECK_COMPLETE);
    }
  };

  static View.OnClickListener reEntryAvi = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      final Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          AviManager.getInstance().setLatestAviLocation(GtmsLocation.TAXI_ENTRY);
          StateManager.getInstance().trigger(Event.LATEST_AVI_AT_RE_ENTRY);
        }
      }, 5 * 1000);
    }
  };

  static View.OnClickListener taxiLoopAvi = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      AviManager.getInstance().setLatestAviLocation(GtmsLocation.DTD_RECIRCULATION);
      StateManager.getInstance().trigger(Event.LATEST_AVI_AT_TAXI_LOOP);
    }
  };

  // CID

  static View.OnClickListener entryCid = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      StateManager.getInstance().trigger(Event.LATEST_CID_IS_ENTRY_CID);
    }
  };

  static View.OnClickListener paymentCid = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      StateManager.getInstance().trigger(Event.LATEST_CID_IS_PAYMENT_CID);
    }
  };

  static View.OnClickListener reEntryCid = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      StateManager.getInstance().trigger(Event.LATEST_CID_IS_RE_ENTRY_CID);
    }
  };

  // LOCATION

  static View.OnClickListener dropPassenger(final DebugView debugView) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        fakeLocation(37.622254, -122.409925);
        debugView.updateFakeButtons("Back to SFO",
            insideBufferedExit,
            "Turn off pings",
            turnOffPings,
            "Crash app",
            crash);
      }
    };
  }

  private static void fakeLocation(double lat, double lng) {
    SfoLocationManager.getInstance().debuggingLocation = true;
    Location location = new Location("");
    location.setLatitude(lat);
    location.setLongitude(lng);
    location.setTime(new Date().getTime());
    EventBus.getDefault().post(new LocationRead(location));

    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        SfoLocationManager.getInstance().debuggingLocation = false;
      }
    }, 10 * 1000);
  }

  private static View.OnClickListener insideBufferedExit = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.614494, -122.394642);
    }
  };

  static View.OnClickListener insideTaxiLoopExit = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.6168, -122.3843);
    }
  };

  static View.OnClickListener insideTaxiWaitingZone = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.616424, -122.386107);
    }
  };

  static View.OnClickListener outsideBufferedExit = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.616489, -122.398215);
      fakeLocation(37.616489, -122.398215);
      fakeLocation(37.616489, -122.398215);
    }
  };

  static View.OnClickListener outsideDomExit = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.614319, -122.390206);
    }
  };

  static View.OnClickListener outsideTaxiWaitingZone = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.621313, -122.378955);
    }
  };

  static View.OnClickListener domesticDeparture = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.615943, -122.384233);
    }
  };


  // OTHER

  private static View.OnClickListener crash = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      throw new RuntimeException("This is a crash");
    }
  };

  static View.OnClickListener gpsBackOn = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Util.TESTING = true;
      EventBus.getDefault().post(new LocationStatusUpdated(true));
      StateManager.getInstance().trigger(Event.GPS_ENABLED);
    }
  };

  // TRIP

  static View.OnClickListener invalidateTrip = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      TripManager.getInstance().invalidate(ValidationStep.GEOFENCE);
    }
  };

  static View.OnClickListener pingOutsideGeofences = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      fakeLocation(37.760661, -122.434092);
      String medallion = DriverManager.getInstance().getCurrentVehicle().medallion;
      Integer vehicleId = DriverManager.getInstance().getCurrentVehicle().vehicleId;
      Integer sessionId = DriverManager.getInstance().getCurrentDriver().sessionId;
      Integer tripId = TripManager.getInstance().getTripId();
      Location outsideLocation = new Location("");
      outsideLocation.setLongitude(70.0);
      outsideLocation.setLatitude(50.0);

      final Ping outsidePing = new Ping(outsideLocation, tripId, vehicleId, sessionId, medallion);

      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          EventBus.getDefault().post(new PingCreated(outsidePing));
        }
      }, 5 * 1000);
    }
  };

  static View.OnClickListener startTrip = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      TripManager.getInstance().start(0);
    }
  };

  static View.OnClickListener timeout = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {

        @Override
        public void run() {
          TripManager.getInstance().invalidate(ValidationStep.DURATION);
          StateManager.getInstance().trigger(Event.TIME_EXPIRED);
        }

      }, 3 * 1000); // 3 sec delay
    }
  };

  private static View.OnClickListener turnOffPings = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      PingKiller.getInstance().turnPingsOffForAWhile();
    }
  };

  static View.OnClickListener validateTrip = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      StateManager.getInstance().trigger(Event.TRIP_VALIDATED);
    }
  };
}
