package com.flysfo.shorttrips.model.lotcounter;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pierreexygy on 12/12/17.
 */

public class TransactionLog implements Serializable {
  public TransactionLog(Integer currentTransactionId) {
    this.currentTransactionId = currentTransactionId;
  }

  @SerializedName("current_transaction_id")
  public Integer currentTransactionId;

  @SerializedName("client_session_id")
  Integer clientSessionId;

  @SerializedName("driver_card_id")
  String driverCardId;

  @SerializedName("event_type_id")
  Integer eventTypeId;

  @SerializedName("trip_type")
  String tripType;

  @SerializedName("status")
  Integer status;

  @SerializedName("system_timestamp")
  String system_timestamp;
}