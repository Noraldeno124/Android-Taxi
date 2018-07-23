package com.flysfo.shorttrips.model.terminal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mattluedke on 3/7/16.
 */
public class TerminalSummaryListWrapperWrapper implements Serializable {

  @SerializedName("arrivals")
  public TerminalSummaryListWrapper arrivalsListWrapper;

  @SerializedName("departures")
  public TerminalSummaryListWrapper departuresListWrapper;

  @SerializedName("total_count")
  Integer totalCount;

  @SerializedName("total_delayed_count")
  Integer totalDelayedCount;

  public TerminalSummaryListWrapper getActiveListWrapper() {
    if (arrivalsListWrapper != null) {
      return arrivalsListWrapper;
    } else if (departuresListWrapper != null) {
      return departuresListWrapper;
    } else {
      return null;
    }
  }
}
