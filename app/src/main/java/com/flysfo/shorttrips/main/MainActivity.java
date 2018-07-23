package com.flysfo.shorttrips.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.debug.DebugFragment;
import com.flysfo.shorttrips.geofence.GeofenceManager;
import com.flysfo.shorttrips.location.SfoLocationManager;
import com.flysfo.shorttrips.model.driver.DriverManager;
import com.flysfo.shorttrips.networking.ReachabilityReceiver;
import com.flysfo.shorttrips.ping.PingManager;
import com.flysfo.shorttrips.security.SecurityFragment;
import com.flysfo.shorttrips.service.OnClearFromRecentService;
import com.flysfo.shorttrips.settings.SettingsFragment;
import com.flysfo.shorttrips.statemachine.Event;
import com.flysfo.shorttrips.statemachine.StateManager;
import com.flysfo.shorttrips.trip.TripManager;
import com.flysfo.shorttrips.util.Speaker;
import com.flysfo.shorttrips.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

  // holding references so everything stays alive
  DriverManager driverManager;
  StateManager stateManager;
  GeofenceManager geofenceManager;
  TripManager tripManager;
  Speaker speaker;
  SfoLocationManager locationManager;
  PingManager pingManager;

  private static final int REQUEST_GOOGLE_PLAY_SERVICES = 101;
  public static final int REQUEST_CHECK_SETTINGS = 102;
  public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 103;
  private static final String SETTINGS_FRAGMENT_ID = "settings_fragment_id";
  private static final String SECURITY_FRAGMENT_ID = "security_fragment_id";
  private static final String DEBUG_FRAGMENT_ID = "debug_fragment_id";
  public static boolean active = false;
  private ReachabilityReceiver receiver = new ReachabilityReceiver();

  @BindView(R.id.viewpager_home)
  ViewPager mViewPager;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.tab_layout)
  TabLayout tabLayout;

  private MenuItem settingsMenuItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    toolbar.setLogo(R.drawable.sfo_logo_alpha);
    tabLayout.addTab(tabLayout.newTab().setText(R.string.flights_fragment_name));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.lot_status_fragment_name));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.trip_fragment_name));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        if (mViewPager != null && tab != null) {
          mViewPager.setCurrentItem(tab.getPosition());
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {
      }
    });

    mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));
    mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    mViewPager.setOffscreenPageLimit(2);
    mViewPager.setCurrentItem(Util.startingFromPush() ? 1 : Util.lastKnownMainScreen); // lot or trip
    Util.useKnownPushId();

    driverManager = DriverManager.getInstance(this);
    stateManager = StateManager.getInstance();
    geofenceManager = GeofenceManager.getInstance(this);
    tripManager = TripManager.getInstance();
    speaker = Speaker.getInstance(this);
    locationManager = SfoLocationManager.getInstance(this);
    pingManager = PingManager.getInstance(this);

    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    receiver = new ReachabilityReceiver();
    this.registerReceiver(receiver, filter);

    if (savedInstanceState == null) {
      startLocationService();
    }

    startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
  }

  @Override
  public void onStart() {
    super.onStart();
    active = true;
  }

  @Override
  public void onStop() {
    super.onStop();
    active = false;
  }

  @Override
  protected void onPause() {
    super.onPause();

    Util.useKnownPushId();
    Util.lastKnownMainScreen = mViewPager.getCurrentItem();
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void showFragmentIfPossible(MenuFragment menuFragment) {

    Fragment fragment;
    String fragmentID;

    switch (menuFragment) {
      case DEBUG:
        fragment = new DebugFragment();
        fragmentID = DEBUG_FRAGMENT_ID;
        break;

      case SECURITY:
        fragment = new SecurityFragment();
        fragmentID = SECURITY_FRAGMENT_ID;
        break;

      case SETTINGS:
        fragment = new SettingsFragment();
        fragmentID = SETTINGS_FRAGMENT_ID;
        break;

      default:
        return;
    }

    if (getFragmentManager().findFragmentByTag(fragmentID) == null) {
      getFragmentManager().popBackStack();
      getFragmentManager()
          .beginTransaction()
          .replace(android.R.id.content, fragment, fragmentID)
          .addToBackStack(null)
          .commit();
      mViewPager.setVisibility(View.GONE);
    }
  }

  public boolean settingsMenuItemActive() {
    if (settingsMenuItem != null) {
      return settingsMenuItem.isVisible() && settingsMenuItem.isEnabled();
    } else {
      return false;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    settingsMenuItem = menu.findItem(R.id.settings);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.settings:
        showFragmentIfPossible(MenuFragment.SETTINGS);
        return true;

      case R.id.security:
        showFragmentIfPossible(MenuFragment.SECURITY);
        return true;

      case R.id.debug:
        showFragmentIfPossible(MenuFragment.DEBUG);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed() {

    if (getFragmentManager().getBackStackEntryCount() != 0) {
      // Looking at settings or debug
      getFragmentManager().popBackStack();
      mViewPager.setVisibility(View.VISIBLE);

    } else if (getSupportFragmentManager().getBackStackEntryCount() != 0
        && mViewPager.getCurrentItem() != 0) {

      // Flight status is chosen but user is looking at another tab
      finish();

    } else {
      super.onBackPressed();
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  private void startLocationService() {
    GoogleApiAvailability api = GoogleApiAvailability.getInstance();
    final int code = api.isGooglePlayServicesAvailable(this);
    if (code == ConnectionResult.SUCCESS) {
      onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
    } else if (api.isUserResolvableError(code) &&
        api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            String str = GoogleApiAvailability.getInstance().getErrorString(code);
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            StateManager.getInstance().trigger(Event.GPS_DISABLED);
          }
        })) {
      // wait for onActivityResult call (see below)
    } else {
      String str = GoogleApiAvailability.getInstance().getErrorString(code);
      Toast.makeText(this, str, Toast.LENGTH_LONG).show();
      StateManager.getInstance().trigger(Event.GPS_DISABLED);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch(requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode == Activity.RESULT_OK) {
          SfoLocationManager.getInstance(this).start();
        } else {
          SfoLocationManager.getInstance(this).checkLocation(null);
//          StateManager.getInstance().trigger(Event.GPS_DISABLED);
        }
        break;

      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case Activity.RESULT_OK:
            SfoLocationManager.getInstance(this).start();
            break;
          case Activity.RESULT_CANCELED:
            StateManager.getInstance().trigger(Event.GPS_DISABLED);
            break;
          default:
            break;
        }
        break;

      default:
        super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          // permission was granted, yay!
          SfoLocationManager.getInstance(this).start();

        } else {
          // permission denied, boo!
          StateManager.getInstance().trigger(Event.GPS_DISABLED);
        }
        return;
      }

      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
}
