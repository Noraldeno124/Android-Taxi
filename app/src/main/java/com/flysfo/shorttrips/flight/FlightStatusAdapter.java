package com.flysfo.shorttrips.flight;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.flight.Flight;
import com.flysfo.shorttrips.model.flight.FlightDeserializer;
import com.flysfo.shorttrips.model.flight.FlightStatus;
import com.flysfo.shorttrips.networking.SfoApi;
import com.flysfo.shorttrips.networking.Url;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

/**
 * Created by pierreexygy on 3/16/16.
 */
public class FlightStatusAdapter extends BaseAdapter {
  private Context context;
  private Flight[] flights;
  private static final Integer TIME_CUSHION = 900;

  public FlightStatusAdapter(Context context, Flight[] flights) {
    this.context = context;
    this.flights = flights;
  }

  @Override
  public int getCount() {
    if (flights != null) {
      return flights.length;
    } else {
      return 0;
    }
  }

  @Override
  public Flight getItem(int i) {
    return flights[i];
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {

    ViewHolder holder;
    if (view != null) {
      holder = (ViewHolder) view.getTag();
    } else {
      view = LayoutInflater.from(context).inflate(R.layout.row_flight_status, viewGroup, false);
      holder = new ViewHolder(view);
      view.setTag(holder);
    }

    Flight flight = getItem(i);
    if (flight.getAirline() != null) {
      holder.airlineTextView.setText(flight.getAirline());
    }
    if (flight.getFlightNumber() != null) {
      holder.flightNumberTextView.setText(flight.getFlightNumber());
    }
    if (flight.getScheduledTime() != null) {
      holder.scheduledTimeTextView.setText(FlightDeserializer.flightDateToString(flight
          .getScheduledTime()));
    }
    if (flight.getEstimatedTime() != null) {
      holder.estimatedTimeTextView.setText(FlightDeserializer.flightDateToString(flight
          .getEstimatedTime()));
    }
    holder.adaptFlightStatus(context, flight);

    OkHttpClient picassoClient = SfoApi.getClient();
    Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(picassoClient))
        .build();

    picasso.load(Url.Taxi.Airline.logoPng(flight.getIataCode()))
        .placeholder(R.drawable.sfo_logo_alpha)
        .error(R.drawable.sfo_logo_alpha)
        .into(holder.airlineImageView);

    return view;
  }

  static class ViewHolder {
    @BindView(R.id.flight_list_fragment)
    LinearLayout flightListFragment;

    @BindView(R.id.airline_image_view)
    ImageView airlineImageView;

    @BindView(R.id.airline_text_view)
    TextView airlineTextView;

    @BindView(R.id.flight_number_text_view)
    TextView flightNumberTextView;

    @BindView(R.id.estimated_time_text_view)
    TextView estimatedTimeTextView;

    @BindView(R.id.scheduled_time_text_view)
    TextView scheduledTimeTextView;

    @BindView(R.id.status_image_view)
    ImageView statusImageView;

    @BindView(R.id.status_text_view)
    TextView statusTextView;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }

    public void adaptFlightStatus(Context context, Flight flight) {
      FlightStatus flightStatus;

      if (flight != null && flight.getScheduledTime() != null && flight.getEstimatedTime() != null) {

        if (((flight.getEstimatedTime().getTime() - flight.getScheduledTime().getTime()) / 1000) >
            TIME_CUSHION) {
          flightStatus = FlightStatus.DELAYED;
        } else {
          flightStatus = FlightStatus.ONTIME;
        }

        statusImageView.setImageResource(flightStatus.toCircleAsset());
        statusTextView.setText(flightStatus.toStringRes());
        statusTextView.setTextColor(ContextCompat.getColor(context, flightStatus.toNumberColor()));
      }
    }
  }

  public void refreshData(Flight[] flights) {
    this.flights = flights;
    notifyDataSetChanged();
  }
}
