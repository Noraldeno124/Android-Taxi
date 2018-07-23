package com.flysfo.shorttrips.events;

import java.io.IOException;

import retrofit2.Response;

/**
 * Created by mattluedke on 2/10/16.
 */
public class NetworkResponse {
  public final String urlString;
  public final Integer statusCode;
  public String message;

  public NetworkResponse(Response response) {
    this.urlString = response.raw().request().url().toString();
    this.statusCode = response.code();
    try {
      this.message = (response.errorBody() != null) ? response.errorBody().string()
          : "";
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public NetworkResponse(Throwable t) {
    this.urlString = null;
    this.statusCode = null;
    this.message = t.getMessage();
  }

  public String toString() {
    return (urlString != null ? urlString : "")
        + ": "
        + statusCode
        + ","
        + (message != null ? message : "");
  }
}
