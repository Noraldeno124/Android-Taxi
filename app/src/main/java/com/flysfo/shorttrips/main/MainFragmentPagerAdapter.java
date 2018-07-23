package com.flysfo.shorttrips.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.flysfo.shorttrips.flight.FlightsTabFragment;
import com.flysfo.shorttrips.lot.LotFragment;
import com.flysfo.shorttrips.trip.TripFragment;

/**
 * Created by mattluedke on 1/4/16.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

  public MainFragmentPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return new FlightsTabFragment();
      case 1:
        return new LotFragment();
      case 2:
        return new TripFragment();
      default:
        throw new RuntimeException("unknown viewpager position");
    }
  }

  @Override
  public int getCount() {
    return 3;
  }
}
