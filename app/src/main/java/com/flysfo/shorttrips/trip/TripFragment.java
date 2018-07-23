package com.flysfo.shorttrips.trip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.LocationStatusUpdated;
import com.flysfo.shorttrips.events.PushReceivedEvent;
import com.flysfo.shorttrips.events.ReachabilityEvent;
import com.flysfo.shorttrips.events.TripInvalidated;
import com.flysfo.shorttrips.model.trip.ValidationStep;
import com.flysfo.shorttrips.prefs.SfoPreferences;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.State;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.statemachine.easyflow.ContextHandler;
import com.flysfo.shorttrips.statemachine.easyflow.EasyFlow;
import com.flysfo.shorttrips.statemachine.easyflow.StatefulContext;
import com.flysfo.shorttrips.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mattluedke on 1/4/16.
 */
public class TripFragment extends Fragment {

  private static final int UI_DELAY = 500;

  @BindView(R.id.view_trip)
  TripView tripView;

  private AlertDialog dialog;
  private boolean isActuallyVisible = false;

  private Handler timerHandler = new Handler();
  private static final long timeInterval = 1 * 1000; // 1sec
  private Runnable timerRunnable = new Runnable() {

    @Override
    public void run() {
      StateManager stateManager = StateManager.getInstance();
      stateManager.keepTheMachineMoving();
      boolean isInProgress = stateManager.getState() == State.IN_PROGRESS;
      Long elapsedTime = TripManager.getInstance().getElapsedTime(isInProgress);
      if (elapsedTime != null) {
        tripView.updateCountdown(elapsedTime);
      } else if (isInProgress) {
        Answers.getInstance().logCustom(new CustomEvent("TripFragment IN_PROGRESS null time"));
        StateManager.getInstance().trigger(Event.FAILURE);
      }

      timerHandler.postDelayed(this, timeInterval);
    }
  };

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trip, container, false);
    ButterKnife.bind(this, view);

    setupObservers();

    initializeForState(StateManager.getInstance().getState());

    timerHandler.postDelayed(timerRunnable, 0);

    StateManager.getInstance().keepTheMachineMoving();

    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    isActuallyVisible = isVisibleToUser;
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    tripView.setReachabilityVisibility(!Util.internetConnected(getContext()));

    final ValidationStep pendingFailure = SfoPreferences.pendingFailure(getContext());
    if (pendingFailure != ValidationStep.UNSPECIFIED) {
      final Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              tripView.notifyFailure(pendingFailure);
            }
          });
        }
      }, UI_DELAY);
    }

    final String pendingSuccess = SfoPreferences.pendingSuccess(getContext());
    if (pendingSuccess != null) {
      final Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              tripView.notifySuccess(pendingSuccess);
            }
          });
        }
      }, UI_DELAY);
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  private void initializeForState(State state) {
    boolean currentAudioSetting = SfoPreferences.getAudioPreference(getActivity());
    SfoPreferences.setAudioPreference(getActivity(), false);
    switch (state) {
      case GPS_IS_OFF:
      case NOT_READY:
      case WAITING_IN_HOLDING_LOT:
      case READY:
      case IN_PROGRESS:
        updateForState(state);
        break;
      case WAITING_FOR_EXIT_AVI:
        updateForState(State.READY);
        break;
      case WAITING_FOR_RE_ENTRY_AVI:
      case ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY:
      case WAITING_FOR_RE_ENTRY_CID:
      case VALIDATING_TRIP:
        updateForState(State.IN_PROGRESS);
        break;
      default:
        updateForState(State.NOT_READY);
        break;
    }
    SfoPreferences.setAudioPreference(getActivity(), currentAudioSetting);
  }

  private void updateForState(State state) {
    switch (state) {
      case GPS_IS_OFF:
        tripView.updatePrompt(StatePrompt.TURN_ON_GPS);
        break;
      case NOT_READY:
        tripView.updatePrompt(StatePrompt.GO_TO_SFO);
        break;
      case WAITING_IN_HOLDING_LOT:
        tripView.updatePrompt(StatePrompt.PAY);
        break;
      case READY:
        tripView.updatePrompt(StatePrompt.READY);
        tripView.changeNotificationVisibility(false);
        break;
      case IN_PROGRESS:
        tripView.updatePrompt(StatePrompt.IN_PROGRESS);
        break;
      default:
        break;
    }

    tripView.changeCountdownVisibility(tripInProgress(state));
  }

  private void notifyFail(final ValidationStep validationStep) {
    if (TripManager.getInstance().getTripId() != null) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          tripView.notifyFailure(validationStep);
        }
      });
    }
  }

  private void setupObservers() {

    EasyFlow machine = StateManager.getInstance().getMachine();
    final Activity activity = getActivity();

    machine.addEventHandler(Event.LOGGED_OUT, new ContextHandler() {
      @Override
      public void call(StatefulContext context) throws Exception {
        notifyFail(ValidationStep.USER_LOGOUT);
      }
    });

    for (final State state : State.values()) {
      machine.addEntryHandler(state, new ContextHandler() {
        @Override
        public void call(StatefulContext context) throws Exception {
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              updateForState(state);
            }
          });
        }
      });
    }

    machine.addEventHandler(Event.OUTSIDE_SHORT_TRIP_GEOFENCE, new ContextHandler() {
      @Override
      public void call(StatefulContext context) throws Exception {
        notifyFail(ValidationStep.GEOFENCE);
      }
    });

    machine.addEventHandler(Event.TIME_EXPIRED, new ContextHandler() {
      @Override
      public void call(StatefulContext context) throws Exception {
        notifyFail(ValidationStep.DURATION);
      }
    });

    machine.addEventHandler(Event.TRIP_VALIDATED, new ContextHandler() {
      @Override
      public void call(StatefulContext context) throws Exception {
        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            tripView.notifySuccess(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
              .format(TripManager.getInstance().getEndTime()));
          }
        });
      }
    });
  }

  @Subscribe
  public void pushReceived(final PushReceivedEvent event) {
    if (isActuallyVisible) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {

          if (dialog != null) {
            dialog.dismiss();
            dialog = null;
          }

          AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
          builder.setTitle(event.title);
          builder.setMessage(event.body);
          builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              dialog = null;
            }
          });
          builder.setCancelable(true);
          dialog = builder.create();
          dialog.show();
        }
      });
    }
  }

  @Subscribe
  public void tripInvalidated(TripInvalidated event) {
    ValidationStep[] steps = event.validationSteps;
    if (steps.length > 0) {
      notifyFail(steps[0]);
    } else {
      notifyFail(ValidationStep.UNSPECIFIED);
    }
  }

  @Subscribe
  public void locationStatusUpdated(LocationStatusUpdated event) {
    if (!event.locationActive) {
      notifyFail(ValidationStep.GPS_FAILURE);
    }
  }

  private boolean tripInProgress(State state) {
    return state == State.IN_PROGRESS
        || state == State.WAITING_FOR_RE_ENTRY_CID
        || state == State.ASSOCIATING_DRIVER_AND_VEHICLE_AT_RE_ENTRY
        || state == State.WAITING_FOR_RE_ENTRY_AVI
        || state == State.VALIDATING_TRIP;
  }

  @Subscribe
  public void reachabilityChanged(ReachabilityEvent event) {
    tripView.setReachabilityVisibility(!event.internetReachable);
  }
}
