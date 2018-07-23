package com.flysfo.shorttrips.networking;

import com.flysfo.shorttrips.model.lotcounter.TransactionLog;

/**
 * Created by pierreexygy on 12/12/17.
 */

public interface ValidTransactionLogRunnable {
  void run(TransactionLog transactionLog);
}