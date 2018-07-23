package com.flysfo.shorttrips.lotcounter;

import android.util.Log;

import com.flysfo.shorttrips.events.NetworkResponse;
import com.flysfo.shorttrips.model.lotcounter.Transaction;
import com.flysfo.shorttrips.model.lotcounter.TransactionLog;
import com.flysfo.shorttrips.model.lotcounter.TransactionLogResponse;
import com.flysfo.shorttrips.model.queue.QueueLengthResponse;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.ValidQueueLengthRunnable;
import com.flysfo.shorttrips.networking.ValidRunnable;
import com.flysfo.shorttrips.networking.ValidTransactionLogRunnable;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pierreexygy on 12/12/17.
 */

public class LotClient {

  public static void getGTMSCount(final ValidQueueLengthRunnable callback) {
    SfoApi.getInstance()
        .requestQueueLength().clone()
        .enqueue(new Callback<QueueLengthResponse>() {
          @Override
          public void onResponse(Call<QueueLengthResponse> call, Response<QueueLengthResponse>
              response) {
            if (response.body() != null
                && response.body().response != null
                && response.body().response.getLongQueueLength() != null) {
                  Integer queueLength = response.body().response.getLongQueueLength();
                  callback.run(queueLength);
            }
          }

          @Override
          public void onFailure(Call<QueueLengthResponse> call, Throwable t) {
            EventBus.getDefault().post(new NetworkResponse(t));
          }
        });
  }

  public static void postLotCounter(final int gtmsCount,
                             final ValidRunnable callback) {
    SfoApi.getInstance()
        .postLotCounter(Integer.toString(gtmsCount))
        .enqueue(new Callback<Void>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response) {
            EventBus.getDefault().post(new NetworkResponse(response));
            if (response.isSuccessful()) {
              callback.run();
            }
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t) {
            EventBus.getDefault().post(new NetworkResponse(t));
          }
        });
  }

  public static void postTransaction(final Transaction transaction,
                              final ValidRunnable callback) {
    SfoApi.getInstance()
        .postTransaction(
            transaction.clientSessionId,
            transaction.driverCardId,
            transaction.eventTypeId,
            transaction.tripType,
            transaction.status
        )
        .enqueue(new Callback<Void>() {
          @Override
          public void onResponse(Call<Void> call, Response<Void> response) {
            EventBus.getDefault().post(new NetworkResponse(response));
            if (response.isSuccessful()) {
              callback.run();
            }
          }

          @Override
          public void onFailure(Call<Void> call, Throwable t) {
            EventBus.getDefault().post(new NetworkResponse(t));
          }
        });
  }

  public static void deleteTransaction(final String driverCadId,
                                         final ValidTransactionLogRunnable callback) {
      SfoApi.getInstance()
          .deleteTransaction(driverCadId)
          .enqueue(new Callback<TransactionLog>() {
            @Override
            public void onResponse(Call<TransactionLog> call, Response<TransactionLog>
                response) {
              if (response.isSuccessful()) {
                EventBus.getDefault().post(new NetworkResponse(response));
                TransactionLog transactionLog = response.body();
                if (transactionLog != null && transactionLog!= null) {
                  callback.run(transactionLog);
                }
              }
            }

            @Override
            public void onFailure(Call<TransactionLog> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));
            }
          });
  }

  public static void postTransactionLog(final Integer transactionLogId,
                                   final Transaction transaction,
                                   final ValidRunnable callback) {
      SfoApi.getInstance()
          .postTransactionLog(
              transactionLogId,
              transaction.clientSessionId,
              transaction.driverCardId,
              transaction.eventTypeId,
              transaction.tripType,
              transaction.status
          )
          .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
              EventBus.getDefault().post(new NetworkResponse(response));
              if (response.isSuccessful()) {
                callback.run();
              }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));
            }
          });
    }

    public static void putTransactionLog(final Integer transactionLogId,
                                  final Integer eventTypeId,
                                  final String tripType,
                                  final ValidRunnable callback) {
      SfoApi.getInstance()
          .putTransactionLog(
              transactionLogId,
              eventTypeId,
              tripType
          )
          .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
              EventBus.getDefault().post(new NetworkResponse(response));
              if (response.isSuccessful()) {
                callback.run();
              }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));
            }
          });
    }

    public static void eventTypes(final ValidRunnable callback) {
      SfoApi.getInstance()
          .eventTypes()
          .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
              EventBus.getDefault().post(new NetworkResponse(response));
              if (response.isSuccessful()) {
                callback.run();
              }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
              EventBus.getDefault().post(new NetworkResponse(t));
            }
          });
    }
}

