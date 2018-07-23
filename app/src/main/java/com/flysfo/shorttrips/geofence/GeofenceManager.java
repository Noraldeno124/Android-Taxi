package com.flysfo.shorttrips.geofence;

import android.app.Activity;
import android.location.Location;

import com.flysfo.shorttrips.events.FoundInsideGeofences;
import com.flysfo.shorttrips.events.LocationRead;
import com.flysfo.shorttrips.model.geofence.SfoGeofence;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by mattluedke on 2/3/16.
 */
public class GeofenceManager {

  private static final int ALLOWABLE_READS_OUTSIDE_WAIT_ZONE = 10;
  private static final int INSIDE_SFO_DISTANCE_FILTER = 30;
  private static final int OUTSIDE_SFO_DISTANCE_FILTER = 300;
  private static final int REQUIRED_READS_OUTSIDE_SFO = 3;

  private Activity activity;
  private Boolean insideSfoBufferedExit = false;
  private Location lastCheckedLocation;
  private List<SfoGeofence> lastKnownGeofences;
  private int consecutiveOutsideWaitZone = 0;
  private int consecutiveOutsideSfo = 0;

  private static GeofenceManager instance = null;
  public static GeofenceManager getInstance(Activity activity) {
    if (instance == null) {
      instance = new GeofenceManager(activity);
    } else {
      instance.activity = activity;
    }
    return instance;
  }

  public static GeofenceManager getInstance() {
    if (instance == null) {
      throw new NullPointerException("trying to access GeofenceManager before initialized");
    }
    return instance;
  }

  private GeofenceManager(Activity activity) {
    this.activity = activity;
    EventBus.getDefault().register(this);
  }

  private int distanceFilter() {
    return insideSfoBufferedExit ? INSIDE_SFO_DISTANCE_FILTER : OUTSIDE_SFO_DISTANCE_FILTER;
  }

  public void reset() {
    lastCheckedLocation = null;
  }

  @Subscribe
  public void locationRead(LocationRead event) {

    final Location location = event.location;

    if (lastCheckedLocation == null
          || location.distanceTo(lastCheckedLocation) > distanceFilter()) {

      GeofenceArbiter.requestGeofencesForLocation(activity, location, new GeofenceResponder() {
        @Override
        public void geofencesFound(List<SfoGeofence> geofences) {
          process(geofences);
          EventBus.getDefault().post(new FoundInsideGeofences(geofences));
          lastCheckedLocation = location;
        }
      });
    }
  }

  public void process(List<SfoGeofence> geofences) {

    StateManager stateManager = StateManager.getInstance();

    if (geofences.contains(SfoGeofence.TAXI_EXIT_BUFFERED)) {
      consecutiveOutsideSfo = 0;
      stateManager.trigger(Event.INSIDE_BUFFERED_EXIT);
      insideSfoBufferedExit = true;
    } else {
      consecutiveOutsideSfo += 1;
      if (consecutiveOutsideSfo >= REQUIRED_READS_OUTSIDE_SFO) {
        stateManager.trigger(Event.OUTSIDE_BUFFERED_EXIT);
        insideSfoBufferedExit = false;
      }
    }

    if (geofences.contains(SfoGeofence.SFO_TAXI_DOMESTIC_EXIT)
        && !geofences.contains(SfoGeofence.TAXI_WAITING_ZONE)) {

      stateManager.trigger(Event.INSIDE_TAXI_LOOP_EXIT);
    }

    if (geofences.contains(SfoGeofence.TAXI_WAITING_ZONE)) {
      consecutiveOutsideWaitZone = 0;
      stateManager.trigger(Event.INSIDE_TAXI_WAITING_ZONE);
    } else {
      consecutiveOutsideWaitZone += 1;
      if (consecutiveOutsideWaitZone > ALLOWABLE_READS_OUTSIDE_WAIT_ZONE) {
        stateManager.trigger(Event.OUTSIDE_TAXI_WAIT_ZONE);
      }
    }

    lastKnownGeofences = geofences;
  }

  public Boolean stillInsideTaxiWaitZone() {
    return lastKnownGeofences != null && lastKnownGeofences.contains(SfoGeofence.TAXI_WAITING_ZONE);
  }

  public Boolean stillInDomesticExitNotInHoldingLot() {
    return lastKnownGeofences != null && lastKnownGeofences.contains(SfoGeofence
        .SFO_TAXI_DOMESTIC_EXIT) && !lastKnownGeofences.contains(SfoGeofence.TAXI_WAITING_ZONE);
  }

  public Boolean stillInsideSfoBufferedExit() {
    return insideSfoBufferedExit;
  }

  public List<SfoGeofence> getLastKnownGeofences() {
    return lastKnownGeofences;
  }
}
