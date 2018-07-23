package com.flysfo.shorttrips.flight;

import com.flysfo.shorttrips.model.flight.FlightType;
import com.flysfo.shorttrips.model.terminal.TerminalId;

/**
 * Created by pierreexygy on 3/16/16.
 */
public interface OnTerminalSelectedListener {
  void onTerminalSelected(TerminalId terminalId, Integer currentHour, FlightType flightType);
}
