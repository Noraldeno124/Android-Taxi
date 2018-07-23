package com.flysfo.shorttrips.model.driver;

import android.test.ActivityInstrumentationTestCase2;

import com.flysfo.shorttrips.main.MainActivity;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.Url;

/**
 * Created by mattluedke on 2/9/16.
 */
public class VehicleResponseTest extends ActivityInstrumentationTestCase2<MainActivity> {

  private static final String MOCK_VEHICLE_1 = "{\"response\": {\"vehicle_id\":12999," +
      "\"transponder_id\":2005887,\"gtms_trip_id\":10590,\"license_plate\":\"13702K1\",\"medallion\":1404}}";

  private static final String MOCK_VEHICLE_2 = "{\"response\": {\"vehicle_id\":12999," +
      "\"transponder_id\":2005887,\"gtms_trip_id\":10590,\"license_plate\":\"13702K1\",\"medallion\":\"0737\"}}";

  private static final String BAD_MOCK_VEHICLE = "{\"response\": {\"vehicle_id\":12999," +
      "\"transponder_id\":2005887,\"gtms_trip_id\":10590,\"license_plate\":\"13702K1\"}}";

  MainActivity mainActivity;

  public VehicleResponseTest() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (!Url.isStaging()) {
      throw new RuntimeException("can't use production URL in tests");
    }

    mainActivity = getActivity();
  }

  public void testMedallionAsInt() {
    VehicleResponse vehicleResponse = SfoApi.getGson().fromJson(MOCK_VEHICLE_1, VehicleResponse.class);
    assertNotNull(vehicleResponse);
    assertNotNull(vehicleResponse.response);
    assertNotNull(vehicleResponse.response.medallion);
  }

  public void testMedallionAsString() {
    VehicleResponse vehicleResponse = SfoApi.getGson().fromJson(MOCK_VEHICLE_2, VehicleResponse.class);
    assertNotNull(vehicleResponse);
    assertNotNull(vehicleResponse.response);
    assertNotNull(vehicleResponse.response.medallion);
  }

  public void testBadMedallion() {
    VehicleResponse vehicleResponse = SfoApi.getGson().fromJson(BAD_MOCK_VEHICLE, VehicleResponse.class);
    assertNotNull(vehicleResponse);
    assertNotNull(vehicleResponse.response);
    assertNull(vehicleResponse.response.medallion);
  }
}
