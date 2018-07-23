package com.flysfo.shorttrips.model.terminal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mattluedke on 3/7/16.
 */
public class TerminalSummaryListWrapper implements Serializable {

  @SerializedName("list")
  public TerminalSummary[] terminalSummaries;
}
