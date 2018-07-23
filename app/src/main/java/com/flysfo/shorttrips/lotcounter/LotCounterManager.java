package com.flysfo.shorttrips.lotcounter;

import android.util.Log;

import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.lotcounter.Transaction;
import com.flysfo.shorttrips.model.lotcounter.TransactionLog;
import com.flysfo.shorttrips.networking.ValidQueueLengthRunnable;
import com.flysfo.shorttrips.networking.ValidRunnable;
import com.flysfo.shorttrips.networking.ValidTransactionLogRunnable;


/**
 * Created by pierreexygy on 12/12/17.
 */

public class LotCounterManager {
  private static LotCounterManager instance = null;
  private LotCounterManager() {}
  public static LotCounterManager getInstance() {
    if (instance == null) {
      instance = new LotCounterManager();
    }
    return instance;
  }

  public void entersGarage(DriverResponse.Driver driver) {
    this.reentersGarage(driver, "");
  }

  public void reentersGarage(final DriverResponse.Driver driver, final String tripType) {
    final Integer event_type_id = 1000;
    final Integer status = 1;

    final Transaction transaction = new Transaction(
        driver.sessionId,
        driver.cardId,
        event_type_id,
        tripType,
        status
    );

    LotClient.getGTMSCount(new ValidQueueLengthRunnable() {
      @Override
      public void run(final Integer queueLength) {
        LotClient.postTransaction(transaction, new ValidRunnable() {
          @Override
          public void run() {
            LotClient.postLotCounter(queueLength, new ValidRunnable() {
              @Override
              public void run() {}
            });
          }
        });
      }
    });
  }

  public void existsGarageThroughCurbside(DriverResponse.Driver driver) {
    this.exitsGarage(driver, 3000);
  }


  public void existsGarageThroughFreeway(DriverResponse.Driver driver) {
    this.exitsGarage(driver, 2000);
  }

  public void exitsGarage(final DriverResponse.Driver driver, final Integer eventTypeId) {
    LotClient.getGTMSCount(new ValidQueueLengthRunnable() {
      @Override
      public void run(final Integer queueLength) {
        LotClient.deleteTransaction(driver.cardId, new ValidTransactionLogRunnable() {
          @Override
          public void run(final TransactionLog transactionLog) {
            DriverManager.getInstance().setCurrentTransactionLog(transactionLog);
            LotClient.postLotCounter(queueLength, new ValidRunnable() {
              @Override
              public void run() {
                LotClient.putTransactionLog(transactionLog.currentTransactionId, eventTypeId,null,
                     new ValidRunnable() {
                      @Override
                      public void run() {}
                    }) ;
              }
            });
          }
        });
      }
    });
  }

  public void exitsAirport(DriverResponse.Driver driver) {
    final Integer event_type_id = 4000; // exit airport
    final Integer status = 0; // exit airport

    final Transaction transaction = new Transaction(
        driver.sessionId,
        driver.cardId,
        event_type_id,
        null,
        status
    );

    TransactionLog currentTransactionLog = DriverManager.getInstance().getCurrentTransactionLog();

    if (currentTransactionLog != null && currentTransactionLog.currentTransactionId != null){
      LotClient.postTransactionLog(currentTransactionLog.currentTransactionId, transaction, new ValidRunnable() {
        @Override
        public void run() {}
      });
    }
  }
}
