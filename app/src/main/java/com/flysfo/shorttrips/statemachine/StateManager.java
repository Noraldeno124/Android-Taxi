package com.flysfo.shorttrips.statemachine;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.handlers.CheckingForExitAvi;
import com.flysfo.shorttrips.handlers.LoggedOut;
import com.flysfo.shorttrips.handlers.NotReady;
import com.flysfo.shorttrips.handlers.Ready;
import com.flysfo.shorttrips.handlers.TripStarter;
import com.flysfo.shorttrips.handlers.TripValidator;
import com.flysfo.shorttrips.handlers.WaitingInHoldingLot;
import com.flysfo.shorttrips.polling.AssociationAtEntry;
import com.flysfo.shorttrips.polling.AssociationAtHoldingLot;
import com.flysfo.shorttrips.polling.AssociationAtReEntry;
import com.flysfo.shorttrips.polling.WaitingForEntryAvi;
import com.flysfo.shorttrips.polling.WaitingForEntryCid;
import com.flysfo.shorttrips.polling.WaitingForPaymentCid;
import com.flysfo.shorttrips.polling.WaitingForReEntryAvi;
import com.flysfo.shorttrips.polling.WaitingForReEntryCid;
import com.flysfo.shorttrips.polling.WaitingForTaxiLoopAvi;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.EasyFlow;
import com.flysfo.shorttrips.statemachine.easyflow.EventEnum;
import com.flysfo.shorttrips.statemachine.easyflow.EventHandler;
import com.flysfo.shorttrips.statemachine.easyflow.FlowBuilder;
import com.flysfo.shorttrips.statemachine.easyflow.LogicViolationError;
import com.flysfo.shorttrips.statemachine.easyflow.StateEnum;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattluedke on 2/1/16.
 */
public class StateManager {
  private static StateManager instance = null;
  public static StateManager getInstance() {
    if (instance == null) {
      instance = new StateManager();
    }
    return instance;
  }

  private EasyFlow flow;
  private StatefulContext context;
  private List<Event> pendingEvents = new ArrayList<>();

  public EasyFlow getMachine() {
    return flow;
  }

  public State getState() {
    return (State)context.getState();
  }

  private void removeEventAndMoveToNext(EventEnum event) {
    //noinspection SuspiciousMethodCalls
    pendingEvents.remove(event);

    if (pendingEvents.size() > 0) {
      triggerAndRemoveIfFailed(pendingEvents.get(0));
    }
  }

  private void triggerAndRemoveIfFailed(Event event) {
    try {
      flow.trigger(event, context);
    } catch (LogicViolationError logicViolationError) {
      removeEventAndMoveToNext(event);
    } catch (Exception e) {
      Answers.getInstance().logCustom(new CustomEvent("triggerAndRemoveIfFailed Exception: " +
          event.name() + " " + e.getMessage()));
      removeEventAndMoveToNext(event);
    }
  }

  public void trigger(final Event event) {

    addHandler();

    pendingEvents.add(event);

    if (pendingEvents.size() > 0) {
      triggerAndRemoveIfFailed(pendingEvents.get(0));
    }
  }

  private void addHandler() {
    flow.whenEvent(new EventHandler() {
      @Override
      public void call(final EventEnum calledEvent, StateEnum from, final StateEnum to, StatefulContext
          context) throws Exception {
        final State desiredState = (State) to;
        flow.addEntryHandler(desiredState, new ContextHandler() {
          @Override
          public void call(StatefulContext context) throws Exception {
            flow.removeEntryHandler(desiredState, this);
            removeEventAndMoveToNext(calledEvent);
          }
        });
      }
    });
  }

  public void keepTheMachineMoving() {
    if (pendingEvents.size() > 0) {
      addHandler();
      triggerAndRemoveIfFailed(pendingEvents.get(0));
    }
  }

  private StateManager() {

    flow = FlowBuilder.from(State.NOT_READY).transit(

        FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF).transit(
            FlowBuilder.on(Event.GPS_ENABLED).to(State.NOT_READY)
        ),
        FlowBuilder.on(Event.INSIDE_TAXI_LOOP_EXIT).to(State.WAITING_FOR_PAYMENT_CID),
        FlowBuilder.on(Event.INSIDE_TAXI_WAITING_ZONE).to(State.WAITING_FOR_ENTRY_CID).transit(

            FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
            FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
            FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
            FlowBuilder.on(Event.INSIDE_TAXI_LOOP_EXIT).to(State.WAITING_FOR_PAYMENT_CID),
            FlowBuilder.on(Event.LATEST_CID_IS_ENTRY_CID).to(State
                .ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY).transit(

                FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                FlowBuilder.on(Event.DRIVER_AND_VEHICLE_ASSOCIATED).to(State
                    .WAITING_FOR_ENTRY_AVI).transit(

                    FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                    FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                    FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                    FlowBuilder.on(Event.INSIDE_TAXI_LOOP_EXIT).to(State.WAITING_FOR_PAYMENT_CID),
                    FlowBuilder.on(Event.LATEST_AVI_AT_ENTRY).to(State
                        .WAITING_IN_HOLDING_LOT).transit(

                        FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                        FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                        FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                        FlowBuilder.on(Event.INSIDE_TAXI_LOOP_EXIT).to(State
                            .WAITING_FOR_PAYMENT_CID).transit(

                            FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                            FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                            FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                            FlowBuilder.on(Event.LATEST_CID_IS_PAYMENT_CID).to(State
                                .ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT).transit(

                                FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                                FlowBuilder.on(Event.DRIVER_AND_VEHICLE_ASSOCIATED).to
                                    (State.WAITING_FOR_TAXI_LOOP_AVI).transit(

                                    FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                                    FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                    FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                    FlowBuilder.on(Event.LATEST_AVI_AT_TAXI_LOOP).to(State
                                        .READY).transit(

                                        FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                                        FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                        FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                        FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                                        FlowBuilder.on(Event.OUTSIDE_BUFFERED_EXIT).to(State
                                            .WAITING_FOR_EXIT_AVI).transit(

                                            FlowBuilder.on(Event.APP_QUIT).to(State.NOT_READY),
                                            FlowBuilder.on(Event.EXIT_AVI_CHECK_COMPLETE).to
                                                (State.STARTING_TRIP).transit(

                                                FlowBuilder.on(Event.APP_QUIT).to(State
                                                    .NOT_READY),
                                                FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                                FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                                FlowBuilder.on(Event.LOGGED_OUT).to(State
                                                    .NOT_READY),
                                                FlowBuilder.on(Event.TRIP_STARTED).to
                                                    (State.IN_PROGRESS).transit(

                                                    FlowBuilder.on(Event.APP_QUIT).to(State
                                                        .NOT_READY),
                                                    FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                                    FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                                    FlowBuilder.on(Event
                                                        .INSIDE_BUFFERED_EXIT).to(State
                                                        .WAITING_FOR_RE_ENTRY_CID).transit(

                                                        FlowBuilder.on(Event.APP_QUIT).to(State
                                                            .NOT_READY),
                                                        FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                                        FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                                        FlowBuilder.on(Event
                                                            .LATEST_CID_IS_RE_ENTRY_CID)
                                                            .to(State
                                                                .ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY).transit(

                                                            FlowBuilder.on(Event.APP_QUIT).to(State
                                                                .NOT_READY),
                                                            FlowBuilder.on(Event
                                                                .DRIVER_AND_VEHICLE_ASSOCIATED).to(State.WAITING_FOR_RE_ENTRY_AVI).transit(

                                                                FlowBuilder.on(Event.APP_QUIT).to(State
                                                                    .NOT_READY),
                                                                FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                                                FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                                                FlowBuilder.on(Event
                                                                    .LATEST_AVI_AT_RE_ENTRY).to(State.VALIDATING_TRIP).transit(

                                                                    FlowBuilder.on(Event.APP_QUIT).to(State
                                                                        .NOT_READY),
                                                                    FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                                                    FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                                                    FlowBuilder.on(Event
                                                                        .LOGGED_OUT).to(State.NOT_READY),
                                                                    FlowBuilder.on(Event
                                                                        .OUTSIDE_SHORT_TRIP_GEOFENCE).to
                                                                        (State.NOT_READY),
                                                                    FlowBuilder.on(Event.TIME_EXPIRED).to
                                                                        (State.NOT_READY),
                                                                    FlowBuilder.on(Event
                                                                        .TRIP_INVALIDATED).to
                                                                        (State.NOT_READY),
                                                                    FlowBuilder.on(Event
                                                                        .TRIP_VALIDATED)
                                                                        .to(State.WAITING_IN_HOLDING_LOT)
                                                                ),
                                                                FlowBuilder.on(Event
                                                                    .LOGGED_OUT).to(State.NOT_READY),
                                                                FlowBuilder.on(Event
                                                                    .NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK).to(State.IN_PROGRESS),
                                                                FlowBuilder.on(Event
                                                                    .OUTSIDE_SHORT_TRIP_GEOFENCE).to
                                                                    (State.NOT_READY),
                                                                FlowBuilder.on(Event.TIME_EXPIRED).to
                                                                    (State.NOT_READY)
                                                            ),
                                                            FlowBuilder.on(Event.FAILURE).to
                                                                (State.NOT_READY),
                                                            FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                                            FlowBuilder.on(Event.LOGGED_OUT)
                                                                .to(State.NOT_READY),
                                                            FlowBuilder.on(Event
                                                                .NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK).to(State.IN_PROGRESS),
                                                            FlowBuilder.on(Event
                                                                .OUTSIDE_SHORT_TRIP_GEOFENCE).to
                                                                (State.NOT_READY),
                                                            FlowBuilder.on(Event.TIME_EXPIRED).to
                                                                (State.NOT_READY)
                                                        ),
                                                        FlowBuilder.on(Event.LOGGED_OUT).to
                                                            (State.NOT_READY),
                                                        FlowBuilder.on(Event
                                                            .NOT_INSIDE_BUFFERED_EXIT_AFTER_FAILED_RE_ENTRY_CHECK).to(State.IN_PROGRESS),
                                                        FlowBuilder.on(Event
                                                            .OUTSIDE_SHORT_TRIP_GEOFENCE).to
                                                            (State.NOT_READY),
                                                        FlowBuilder.on(Event.TIME_EXPIRED).to(State.NOT_READY)
                                                    ),
                                                    FlowBuilder.on(Event.LOGGED_OUT).to
                                                        (State.NOT_READY),
                                                    FlowBuilder.on(Event
                                                        .OUTSIDE_SHORT_TRIP_GEOFENCE).to
                                                        (State.NOT_READY),
                                                    FlowBuilder.on(Event.TIME_EXPIRED).to(State.NOT_READY)
                                                )
                                            ),
                                            FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                            FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                            FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY)
                                        )
                                    ),
                                    FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                                    FlowBuilder.on(Event
                                        .NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS).to(State.WAITING_IN_HOLDING_LOT),
                                    FlowBuilder.on(Event
                                        .NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_NOT_ENTRY_OR_STATUS).to(State.NOT_READY)
                                ),
                                FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                                FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                                FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                                FlowBuilder.on(Event
                                    .NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS).to(State.WAITING_IN_HOLDING_LOT),
                                FlowBuilder.on(Event
                                    .NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_NOT_ENTRY_OR_STATUS).to(State.NOT_READY)
                            ),
                            FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                            FlowBuilder.on(Event
                                .NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_ENTRY_OR_STATUS).to(State.WAITING_IN_HOLDING_LOT),
                            FlowBuilder.on(Event
                                .NOT_INSIDE_TAXI_LOOP_EXIT_AFTER_FAILED_PAYMENT_CHECK_LATEST_AVI_NOT_ENTRY_OR_STATUS).to(State.NOT_READY)
                        ),
                        FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                        FlowBuilder.on(Event.OUTSIDE_TAXI_WAIT_ZONE).to(State.NOT_READY)
                    ),
                    FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                    FlowBuilder.on(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK).to(State.NOT_READY)
                ),
                FlowBuilder.on(Event.FAILURE).to(State.NOT_READY),
                FlowBuilder.on(Event.GPS_DISABLED).to(State.GPS_IS_OFF),
                FlowBuilder.on(Event.INSIDE_TAXI_LOOP_EXIT).to(State.WAITING_FOR_PAYMENT_CID),
                FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
                FlowBuilder.on(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK).to(State.NOT_READY)
            ),
            FlowBuilder.on(Event.LOGGED_OUT).to(State.NOT_READY),
            FlowBuilder.on(Event.NOT_INSIDE_TAXI_WAIT_ZONE_AFTER_FAILED_ENTRY_CHECK).to(State.NOT_READY)
        )
    );

    flow.addEntryHandler(State.NOT_READY, new NotReady());
    flow.addPoller(State.WAITING_FOR_ENTRY_CID, new WaitingForEntryCid());
    flow.addPoller(State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY, new AssociationAtEntry());
    flow.addPoller(State.WAITING_FOR_ENTRY_AVI, new WaitingForEntryAvi());
    flow.addEntryHandler(State.WAITING_IN_HOLDING_LOT, new WaitingInHoldingLot());
    flow.addPoller(State.WAITING_FOR_PAYMENT_CID, new WaitingForPaymentCid());
    flow.addPoller(State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT, new AssociationAtHoldingLot());
    flow.addPoller(State.WAITING_FOR_TAXI_LOOP_AVI, new WaitingForTaxiLoopAvi());
    flow.addEntryHandler(State.READY, new Ready());
    flow.addEntryHandler(State.WAITING_FOR_EXIT_AVI, new CheckingForExitAvi());
    flow.addEntryHandler(State.STARTING_TRIP, new TripStarter());
    flow.addPoller(State.WAITING_FOR_RE_ENTRY_CID, new WaitingForReEntryCid());
    flow.addPoller(State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY, new AssociationAtReEntry());
    flow.addPoller(State.WAITING_FOR_RE_ENTRY_AVI, new WaitingForReEntryAvi());
    flow.addEntryHandler(State.VALIDATING_TRIP, new TripValidator());

    flow.addEventHandler(Event.LOGGED_OUT, new LoggedOut());

    context = new StatefulContext();
    flow.start(true, context);
  }
}
