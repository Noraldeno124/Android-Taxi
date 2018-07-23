package com.flysfo.shorttrips.networking;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mattluedke on 12/16/15.
 */
public class HeaderInterceptor implements Interceptor {
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Request newRequest;

    // TODO: add driver, medallion, vehicle_id
    newRequest = request.newBuilder()
      //  .addHeader("", "") // TODO: add driver, medallion, vehicle_id
        .build();

    return chain.proceed(newRequest);
  }
}
