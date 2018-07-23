package com.flysfo.shorttrips.model.terminal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by mattluedke on 3/7/16.
 */
public class TerminalSummary implements Serializable {

  public static TerminalSummary findInArray(TerminalSummary[] summaries, TerminalId terminalId) {
    for (TerminalSummary summary : summaries) {
      if (summary.getTerminalId() != null
          && summary.getTerminalId() == terminalId
          && summary.onTimeCount != null
          && summary.delayedCount != null) {
        return summary;
      }
    }

    throw new RuntimeException("invalid summaries " + Arrays.toString(summaries));
  }

  @SerializedName("terminal_id")
  public Integer terminalId;

  @SerializedName("delayed_count")
  public Integer delayedCount;

  @SerializedName("count")
  public Integer onTimeCount;

  public TerminalId getTerminalId() {
    return TerminalId.fromInt(terminalId);
  }
}
