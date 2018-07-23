package com.flysfo.shorttrips.debug;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.FoundInsideGeofences;
import com.flysfo.shorttrips.events.GtmsCallMade;
import com.flysfo.shorttrips.events.LocationRead;
import com.flysfo.shorttrips.events.LocationStatusUpdated;
import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.events.PingCreated;
import com.flysfo.shorttrips.events.PingInvalid;
import com.flysfo.shorttrips.events.PingKillerActive;
import com.flysfo.shorttrips.events.PingValid;
import com.flysfo.shorttrips.events.ReachabilityEvent;
import com.flysfo.shorttrips.events.SessionReloaded;
import com.flysfo.shorttrips.events.TripInvalidated;
import com.flysfo.shorttrips.events.TripValidated;
import com.flysfo.shorttrips.events.UnexpectedGtms;
import com.flysfo.shorttrips.geofence.GeofenceManager;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DebugFragment extends Fragment {

  @BindView(R.id.view_debug)
  DebugView debugView;

  private ContextHandler stateChanger = new ContextHandler() {
    @Override
    public void call(final StatefulContext context) throws Exception {
      if (isResumed() && isVisible()) {
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            updateForState((State) context.getState());
          }
        });
      }
    }
  };

  private static String debugText;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_debug, container, false);
    ButterKnife.bind(this, view);

    debugView.updateGeofenceList(GeofenceManager.getInstance().getLastKnownGeofences());
    debugView.updateLocation(SfoLocationManager.getInstance().getLastKnownLocation());

    debugView.consoleTextView.setText(debugText);
    updateForState(StateManager.getInstance().getState());

    for (State state : State.values()) {
      StateManager.getInstance().getMachine().addEntryHandler(state, stateChanger);
    }

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    debugText = debugView.consoleTextView.getText().toString();
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    debugView.setReachabilityVisibility(!Util.internetConnected(getActivity()));
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Subscribe
  public void reachabilityChanged(ReachabilityEvent event) {
    debugView.setReachabilityVisibility(!event.internetReachable);
    if (event.internetReachable) {
      debugView.printDebugLine("Internet reachable", DebugType.POSITIVE);
    } else {
      debugView.printDebugLine("Internet unreachable", DebugType.NEGATIVE);
    }
  }

  @Subscribe
  public void gtmsCallMade(GtmsCallMade event) {
    debugView.incrementGtms();
  }

  @Subscribe
  public void sessionReloaded(SessionReloaded event) {
    debugView.printDebugLine("session reloaded", DebugType.POSITIVE);
  }

  @Subscribe
  public void pingCreated(PingCreated event) {
    debugView.printDebugLine("created ping " + event.ping.timestamp);
  }

  @Subscribe
  public void pingInvalid(PingInvalid event) {
    debugView.printDebugLine("invalid ping " + event.ping.timestamp, DebugType.NEGATIVE);
  }

  @Subscribe
  public void pingKillerActive(PingKillerActive event) {
    debugView.printDebugLine("pings disabled for 2 min");
  }

  @Subscribe
  public void pingValid(PingValid event) {
    debugView.printDebugLine("valid ping " + event.ping.timestamp, DebugType.POSITIVE);
  }

  @Subscribe
  public void unexpectedGtms(UnexpectedGtms event) {
    debugView.printDebugLine(event.message(), DebugType.NEGATIVE);
  }

  @Subscribe
  public void geofencesFound(FoundInsideGeofences event) {
    debugView.updateGeofenceList(event.geofences);
  }

  @Subscribe
  public void tripValidated(TripValidated event) {
    debugView.printDebugLine("trip " + event.tripId + " validated", DebugType.POSITIVE);
  }

  @Subscribe
  public void tripInvalidated(TripInvalidated event) {
    String debugLine = "trip " + event.tripId + " invalidated: ";
    for (ValidationStep step : event.validationSteps) {
      debugLine += step + ",";
    }

    debugView.printDebugLine(debugLine, DebugType.NEGATIVE);
  }

  @Subscribe
  public void networkResponse(NetworkResponse event) {
    DebugType debugType = DebugType.NEUTRAL;

    if (event.statusCode != null) {
      debugType = (event.statusCode >= 200 && event.statusCode <= 299)
          ? DebugType.POSITIVE
          : DebugType.NEGATIVE;
    }

    debugView.printDebugLine(event.toString(), debugType);
  }

  @Subscribe
  public void locationStatusUpdated(LocationStatusUpdated event) {
    if (!event.locationActive) {
      debugView.printDebugLine("GPS off", DebugType.NEGATIVE);
    }
  }

  @Subscribe
  public void locationRead(LocationRead event){
    debugView.updateLocation(event.location);
  }

  private void updateForState(State state) {

    debugView.printDebugLine(state.name());
    debugView.updateState(state.name());

    switch (state) {
      case GPS_IS_OFF:
        debugView.updateFakeButtons("Fake GPS Back On",
            DebugActions.gpsBackOn);
        break;

      case NOT_READY:
        debugView.updateFakeButtons("Fake Inside Taxi Wait Zone",
            DebugActions.insideTaxiWaitingZone,
            "At Terminal Exit",
            DebugActions.insideTaxiLoopExit,
            "Dom. Departure",
            DebugActions.domesticDeparture);
        break;

      case WAITING_FOR_ENTRY_CID:
        debugView.updateFakeButtons("Trigger Cid Entry",
            DebugActions.entryCid,
            "At Terminal Exit",
            DebugActions.insideTaxiLoopExit,
            "Outside Taxi WaitZone",
            DebugActions.outsideTaxiWaitingZone);
        break;

      case ASSOCIATING_DRIVER_AND_VEHICLE_AT_ENTRY:
        debugView.updateFakeButtons("Associate Driver+Vehicle",
            DebugActions.associateDriverAndVehicle,
            "At Terminal Exit",
            DebugActions.insideTaxiLoopExit,
            "Outside Taxi WaitZone",
            DebugActions.outsideTaxiWaitingZone);
        break;

      case WAITING_FOR_ENTRY_AVI:
        debugView.updateFakeButtons("Entry Gate AVI Read",
            DebugActions.entryAvi,
            "At Terminal Exit",
            DebugActions.insideTaxiLoopExit,
            "Outside Taxi WaitZone",
            DebugActions.outsideTaxiWaitingZone);
        break;

      case WAITING_IN_HOLDING_LOT:
        debugView.updateFakeButtons("At Terminal Exit",
            DebugActions.insideTaxiLoopExit,
            "Outside Taxi WaitZone",
            DebugActions.outsideTaxiWaitingZone);
        break;

      case WAITING_FOR_PAYMENT_CID:
        debugView.updateFakeButtons("Payment CID",
            DebugActions.paymentCid,
            "Outside DomExit",
            DebugActions.outsideDomExit);
        break;

      case ASSOCIATING_DRIVER_AND_VEHICLE_AT_HOLDING_LOT_EXIT:
        debugView.updateFakeButtons("Associate Driver+Vehicle",
            DebugActions.associateDriverAndVehicle,
            "Outside DomExit",
            DebugActions.outsideDomExit);
        break;

      case WAITING_FOR_TAXI_LOOP_AVI:
        debugView.updateFakeButtons("Taxi Loop AVI",
            DebugActions.taxiLoopAvi,
            "Outside DomExit",
            DebugActions.outsideDomExit);
        break;

      case READY:
        debugView.updateFakeButtons("Out Of Buffered Exit",
            DebugActions.outsideBufferedExit);
        break;

      case WAITING_FOR_EXIT_AVI:
        debugView.updateFakeButtons("Exit AVI",
            DebugActions.exitAvi);
        break;

      case STARTING_TRIP:
        debugView.updateFakeButtons("Trip Started",
            DebugActions.startTrip);
        break;

      case IN_PROGRESS:
        debugView.updateFakeButtons("Drop Passenger",
            DebugActions.dropPassenger(debugView),
            "Timeout",
            DebugActions.timeout,
            "Outside Geofences",
            DebugActions.pingOutsideGeofences);
        break;

      case WAITING_FOR_RE_ENTRY_CID:
        debugView.updateFakeButtons("ReEntry CID",
            DebugActions.reEntryCid,
            "Outside Buffered Exit",
            DebugActions.outsideBufferedExit);
        break;

      case ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY:
        debugView.updateFakeButtons("Associate Driver+Vehicle",
            DebugActions.associateDriverAndVehicle,
            "Outside Buffered Exit",
            DebugActions.outsideBufferedExit);
        break;

      case WAITING_FOR_RE_ENTRY_AVI:
        debugView.updateFakeButtons("ReEntry AVI",
            DebugActions.reEntryAvi,
            "Outside Buffered Exit",
            DebugActions.outsideBufferedExit);
        break;

      case VALIDATING_TRIP:
        debugView.updateFakeButtons("Validate trip",
            DebugActions.validateTrip,
            "Invalidate",
            DebugActions.invalidateTrip);
        break;

      default:
        break;
    }
  }
}
