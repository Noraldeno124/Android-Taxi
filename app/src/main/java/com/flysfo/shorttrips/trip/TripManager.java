package com.flysfo.shorttrips.trip;

import android.os.Handler;

import com.flysfo.shorttrips.events.TripInvalidated;
import com.flysfo.shorttrips.events.TripValidated;
import com.flysfo.shorttrips.model.trip.MobileState;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.ping.PingManager;
import com.flysfo.shorttrips.service.InvalidateCallback;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by mattluedke on 2/11/16.
 */
public class TripManager {
  private static TripManager instance = null;

  private TripManager() {
  }

  public static TripManager getInstance() {
    if (instance == null) {
      instance = new TripManager();
    }
    return instance;
  }

  private Date startTime;
  private Date endTime;
  private Integer tripId;
  private Handler timerHandler = new Handler();
  private Boolean rightAfterValidShort = false;
  private static final long timeInterval = 5 * 1000; // 5sec
  static final long TRIP_LENGTH_LIMIT = 2 * 60 * 60 * 1000; // 2 hours

  private Runnable timerRunnable = new Runnable() {
    @Override
    public void run() {
      if (getElapsedTime() == null || getElapsedTime() > TRIP_LENGTH_LIMIT) {
        StateManager.getInstance().trigger(Event.TIME_EXPIRED);
        invalidate(ValidationStep.DURATION);
      } else {
        timerHandler.postDelayed(this, timeInterval);
      }
    }
  };

  private Long getElapsedTime() {
    return getElapsedTime(false);
  }

  Long getElapsedTime(boolean force) {
    if (startTime == null) {
      if (force) {
        startTime = new Date();
      } else {
        return null;
      }
    }
    return new Date().getTime() - startTime.getTime();
  }

  public Boolean getRightAfterValidShort() {
    return rightAfterValidShort;
  }

  public Integer getTripId() {
    return tripId;
  }

  private void reset(Boolean rightAfterValidShort) {
    PingManager.getInstance().stop();
    timerHandler.removeCallbacks(timerRunnable);
    tripId = null;
    startTime = null;
    this.rightAfterValidShort = rightAfterValidShort;
  }

  public void setStartTime(Date time) {
    this.startTime = time;
  }

  public Date getStartTime() {
    return this.startTime;
  }

  Date getEndTime() {
    return this.endTime;
  }

  public void start(Integer tripId) {

    this.tripId = tripId;

    if (startTime == null) {
      startTime = new Date();
    }

    PingManager.getInstance().start();

    timerHandler.removeCallbacks(timerRunnable);
    timerHandler.postDelayed(timerRunnable, 0);

    StateManager.getInstance().trigger(Event.TRIP_STARTED);
    MobileState.lastKnown = MobileState.IN_PROGRESS;

    endTime = null;
  }

  public void validate() {
    EventBus.getDefault().post(new TripValidated(tripId));
    StateManager.getInstance().trigger(Event.TRIP_VALIDATED);
    endTime = new Date();
    reset(true);
    MobileState.lastKnown = MobileState.VALID;
  }

  public void invalidated(ValidationStep[] validationSteps) {
    EventBus.getDefault().post(new TripInvalidated(tripId, validationSteps));
    StateManager.getInstance().trigger(Event.TRIP_INVALIDATED);
    reset(false);
  }

  public void invalidate(ValidationStep validationStep) {
    invalidate(validationStep, null);
  }

  public void invalidate(ValidationStep validationStep, final InvalidateCallback callback) {

    EventBus.getDefault().post(new TripInvalidated(tripId, new ValidationStep[]{validationStep}));
    StateManager.getInstance().trigger(Event.TRIP_INVALIDATED);
    MobileState.lastKnown = MobileState.INVALID;

    Integer tripId = getTripId();

    if (tripId != null) {
      TripInvalidator.makeInvalidateCall(
        tripId,
        TripDateTransform.fromNow(),
        validationStep.toInt(),
        callback);

      reset(false);

    } else if (callback != null) {
      callback.invalidated();
    }
  }
}
