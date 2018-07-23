package com.flysfo.shorttrips.networking;

import com.flysfo.shorttrips.model.CidResponse;
import com.flysfo.shorttrips.model.DateTimeDeserializer;
import com.flysfo.shorttrips.model.antenna.AntennaResponse;
import com.flysfo.shorttrips.model.dispatcher.Cone;
import com.flysfo.shorttrips.model.dispatcher.ConeDeserializer;
import com.flysfo.shorttrips.model.dispatcher.ConeResponse;
import com.flysfo.shorttrips.model.driver.DriverCredential;
import com.flysfo.shorttrips.model.driver.DriverResponse;
import com.flysfo.shorttrips.model.driver.VehicleResponse;
import com.flysfo.shorttrips.model.flight.Flight;
import com.flysfo.shorttrips.model.flight.FlightDeserializer;
import com.flysfo.shorttrips.model.flight.FlightRequest;
import com.flysfo.shorttrips.model.flight.FlightResponse;
import com.flysfo.shorttrips.model.lotcounter.LotCounterGtmsCount;
import com.flysfo.shorttrips.model.lotcounter.Transaction;
import com.flysfo.shorttrips.model.lotcounter.TransactionLog;
import com.flysfo.shorttrips.model.lotcounter.TransactionLogResponse;
import com.flysfo.shorttrips.model.ping.Ping;
import com.flysfo.shorttrips.model.ping.PingBatch;
import com.flysfo.shorttrips.model.queue.QueueLengthResponse;
import com.flysfo.shorttrips.model.security.SecurityPageData;
import com.flysfo.shorttrips.model.terminal.TerminalSummaryResponse;
import com.flysfo.shorttrips.model.trip.MobileStateInfo;
import com.flysfo.shorttrips.model.trip.TripBody;
import com.flysfo.shorttrips.model.trip.TripIdResponse;
import com.flysfo.shorttrips.model.trip.TripInvalidation;
import com.flysfo.shorttrips.model.trip.TripValidationResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class SfoApi {

  private static final String HEADER_API_KEY = "apikey";
  private static final String STAGING_API_KEY = "Se2wwq4oWy5pxBrqLdsilBXDnscRGZrJ";

  public static SfoApiService getInstance() {
    OkHttpClient httpClient = getClient();

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(Url.base)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(getGson()))
      .client(httpClient)
      .build();

    return retrofit.create(SfoApiService.class);
  }

  public static OkHttpClient getClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader(HEADER_API_KEY, STAGING_API_KEY);
        return chain.proceed(requestBuilder.build());
      }
    });

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    builder.addInterceptor(logging);

    return builder.build();
  }

  public static Gson getGson() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapter(Cone.class, new ConeDeserializer());
    gson.registerTypeAdapter(Date.class, new DateTimeDeserializer());
    gson.registerTypeAdapter(Flight.class, new FlightDeserializer());
    return gson.create();
  }

  @SuppressWarnings("WeakerAccess")
  public interface SfoApiService {

    @GET(Url.securities)
    Call<SecurityPageData> getSecurityPageData();

    // DEVICE

    @GET(Url.Taxi.Device.Avi.transponder)
    Call<AntennaResponse> getAntenna(
      @Path(Url.Taxi.Device.Avi.transponderIdPath) Integer transponderId
    );

    @GET(Url.Taxi.Device.Cid.driver)
    Call<CidResponse> fetchMostRecentCid(
      @Path(Url.Taxi.Device.Cid.driverIdPath) Integer driverId
    );

    // DISPATCHER

    @GET(Url.Taxi.Dispatcher.cone)
    Call<ConeResponse> fetchCone();

    // DRIVER

    @FormUrlEncoded
    @POST(Url.Taxi.Driver.login)
    Call<DriverResponse> authenticateDriver(
      @Field(DriverCredential.Fields.USERNAME) String username,
      @Field(DriverCredential.Fields.PASSWORD) String password,
      @Field(DriverCredential.Fields.LATITUDE) Double latitude,
      @Field(DriverCredential.Fields.LONGITUDE) Double longitude,
      @Field(DriverCredential.Fields.DEVICE_UUID) String deviceUuid,
      @Field(DriverCredential.Fields.OS_VERSION) String osVersion,
      @Field(DriverCredential.Fields.DRIVER_DEVICE_OS) String driverDeviceOs
    );

    @GET(Url.Taxi.Driver.vehicle)
    Call<VehicleResponse> getVehicle(
      @Path(Url.Taxi.Driver.smartCardPath) String smartCard
    );

    // LOT COUNTER

    @FormUrlEncoded
    @POST(Url.Taxi.Lot.lotCounter)
    Call<Void> postLotCounter(
        @Field(LotCounterGtmsCount.Fields.GTMS_COUNT) String gtmsCount
    );

    @FormUrlEncoded
    @POST(Url.Taxi.Lot.currentTransaction)
    Call<Void> postTransaction(
        @Field(Transaction.Fields.CLIENT_SESSION_ID) Integer clientSessionId,
        @Field(Transaction.Fields.DRIVER_CARD_ID) String driverCardIdI,
        @Field(Transaction.Fields.EVENT_TYPE_ID) Integer eventTypeId,
        @Field(Transaction.Fields.TRIP_TYPE) String tripType,
        @Field(Transaction.Fields.STATUS) Integer status
    );

    @DELETE(Url.Taxi.Lot.currentTransactionWithID)
    Call<TransactionLog> deleteTransaction(
        @Path(Url.Taxi.Lot.driverCardId) String driverCardId
    );

    @FormUrlEncoded
    @POST(Url.Taxi.Lot.transactionLog)
    Call<Void> postTransactionLog(
        @Path(Url.Taxi.Lot.transactionLogId) Integer transactionLogId,
        @Field(Transaction.Fields.CLIENT_SESSION_ID) Integer clientSessionId,
        @Field(Transaction.Fields.DRIVER_CARD_ID) String driverCardIdI,
        @Field(Transaction.Fields.EVENT_TYPE_ID) Integer eventTypeId,
        @Field(Transaction.Fields.TRIP_TYPE) String tripType,
        @Field(Transaction.Fields.STATUS) Integer status
    );

    @FormUrlEncoded
    @PUT(Url.Taxi.Lot.transactionLog)
    Call<Void> putTransactionLog(
        @Path(Url.Taxi.Lot.transactionLogId) Integer transactionLogId,
        @Field(Transaction.Fields.EVENT_TYPE_ID) Integer eventTypeId,
        @Field(Transaction.Fields.TRIP_TYPE) String tripType
    );

    @FormUrlEncoded
    @GET(Url.Taxi.Lot.eventType)
    Call<Void> eventTypes();

    // TRIP

    @FormUrlEncoded
    @POST(Url.Taxi.Trip.end)
    Call<TripValidationResponse> endTrip(
      @Path(Url.Taxi.Trip.tripIdPath) Integer tripId,
      @Field(TripBody.DEVICE_TIMESTAMP) String deviceTimestamp,
      @Field(TripBody.DEVICE_UUID) String deviceUuid,
      @Field(TripBody.MEDALLION) String medallion,
      @Field(TripBody.SESSION_ID) Integer sessionId,
      @Field(TripBody.SMART_CARD_ID) String smartCardId,
      @Field(TripBody.VEHICLE_ID) Integer vehicleId
    );

    @FormUrlEncoded
    @POST(Url.Taxi.Trip.ping)
    Call<Void> ping(
      @Path(Url.Taxi.Trip.tripIdPath) Integer tripIdForPath,
      @Field(Ping.GEOFENCE_STATUS_KEY) Integer geofenceStatus,
      @Field(Ping.LATITUDE_KEY) Double latitude,
      @Field(Ping.LONGITUDE_KEY) Double longitude,
      @Field(Ping.MEDALLION_KEY) String medallion,
      @Field(Ping.SESSION_ID_KEY) Integer sessionId,
      @Field(Ping.TIMESTAMP_KEY) String timestamp,
      @Field(Ping.TRIP_ID_KEY) Integer tripId,
      @Field(Ping.VEHICLE_ID_KEY) Integer vehicleId
    );

    @POST(Url.Taxi.Trip.pings)
    Call<Void> pings(
      @Path(Url.Taxi.Trip.tripIdPath) Integer tripId,
      @Body PingBatch pingBatch
    );

    @FormUrlEncoded
    @POST(Url.Taxi.Trip.start)
    Call<TripIdResponse> startTrip(
      @Field(TripBody.DEVICE_TIMESTAMP) String deviceTimestamp,
      @Field(TripBody.DEVICE_UUID) String deviceUuid,
      @Field(TripBody.MEDALLION) String medallion,
      @Field(TripBody.SESSION_ID) Integer sessionId,
      @Field(TripBody.SMART_CARD_ID) String smartCardId,
      @Field(TripBody.VEHICLE_ID) Integer vehicleId
    );

    @FormUrlEncoded
    @POST(Url.Taxi.Trip.invalidate)
    Call<Void> invalidateTrip(
      @Path(Url.Taxi.Trip.tripIdPath) Integer tripId,
      @Field(TripBody.SESSION_ID) Integer sessionId,
      @Field(TripInvalidation.VALIDATION_STEP) Integer validationStep,
      @Field(TripInvalidation.DEVICE_TIMESTAMP) String deviceTimestamp
    );

    @GET(Url.Taxi.Flight.Departure.details)
    Call<FlightResponse> requestDepartureFlightsForTerminal(
      @Query(FlightRequest.TERMINAL) Integer terminal,
      @Query(FlightRequest.HOUR) Integer hour
    );

    @GET(Url.Taxi.Flight.Arrival.details)
    Call<FlightResponse> requestArrivalFlightsForTerminal(
      @Query(FlightRequest.TERMINAL) Integer terminal,
      @Query(FlightRequest.HOUR) Integer hour
    );

    @GET(Url.Taxi.Flight.Departure.summary)
    Call<TerminalSummaryResponse> requestDepartureTerminalSummaries(
      @Query(FlightRequest.HOUR) Integer hour
    );

    @GET(Url.Taxi.Flight.Arrival.summary)
    Call<TerminalSummaryResponse> requestArrivalTerminalSummaries(
      @Query(FlightRequest.HOUR) Integer hour
    );

    @GET(Url.Taxi.Queue.currentLength)
    Call<QueueLengthResponse> requestQueueLength();

    @GET(Url.Taxi.Reference.lotCapacity)
    Call<Integer> requestLotCapacity();

    @FormUrlEncoded
    @PUT(Url.Taxi.Device.mobileStateUpdate)
    Call<Void> updateMobileState(
      @Path(Url.Taxi.Device.stateIdPath) Integer stateId,
      @Field(MobileStateInfo.LATITUDE) Double latitude,
      @Field(MobileStateInfo.LONGITUDE) Double longitude,
      @Field(MobileStateInfo.SESSION_ID) Integer session_id,
      @Field(MobileStateInfo.TRIP_ID) Integer trip_id
    );

    @GET(Url.Taxi.Reference.clientVersion)
    Call<Double> getVersion(
      @Query(Url.Taxi.Reference.platformParam) String platform
    );

    @GET(Url.Taxi.Reference.terms)
    Call<String> getTerms(
      @Query(Url.Taxi.Reference.platformParam) String platform
    );
  }
}
