package com.flysfo.shorttrips.geofence;

import com.flysfo.shorttrips.model.geofence.SfoGeofence;

import java.util.List;

interface GeofenceResponder {
  void geofencesFound(List<SfoGeofence> geofences);
}
