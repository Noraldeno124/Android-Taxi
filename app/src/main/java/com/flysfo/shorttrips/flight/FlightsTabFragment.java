package com.flysfo.shorttrips.flight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.events.PushReceivedEvent;
import com.flysfo.shorttrips.model.flight.FlightType;
import com.flysfo.shorttrips.model.terminal.TerminalId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;

/**
 * Created by pierreexygy on 3/16/16.
 */
public class FlightsTabFragment extends Fragment implements OnTerminalSelectedListener {

  private AlertDialog dialog;
  private boolean isActuallyVisible = false;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_flights_tab, container, false);
    TerminalSummaryFragment terminalSummaryFragment = new TerminalSummaryFragment();
    terminalSummaryFragment.setOnTerminalSelectedListener(this);
    getFragmentManager().beginTransaction()
        .replace(R.id.flights_tab, terminalSummaryFragment)
        .commitAllowingStateLoss();
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onTerminalSelected(TerminalId terminalId, Integer currentHour, FlightType flightType) {
    FlightStatusFragment flightStatusFragment = new FlightStatusFragment();
    flightStatusFragment.setTerminalId(terminalId);
    flightStatusFragment.setCurrentHour(currentHour);
    flightStatusFragment.setFlightType(flightType);

    getFragmentManager().beginTransaction()
        .replace(R.id.flights_tab, flightStatusFragment)
        .addToBackStack(null)
        .commitAllowingStateLoss();
  }

  @Override
  public void onDetach() {
    super.onDetach();

    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);

    isActuallyVisible = isVisibleToUser;

    FragmentManager fragmentManager = getFragmentManager();
    if (fragmentManager.getFragments() != null) {
      for (Fragment fragment : fragmentManager.getFragments()) {
        if (fragment instanceof FlightStatusFragment
            || fragment instanceof TerminalSummaryFragment) {
          fragment.setUserVisibleHint(isVisibleToUser);
        }
      }
    }
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
}
