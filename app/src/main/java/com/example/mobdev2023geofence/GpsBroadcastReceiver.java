package com.example.mobdev2023geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GpsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    // Location provider (GPS) status changed
                    boolean gpsEnabled = isGpsEnabled(context);
                    if (gpsEnabled) {
                        // GPS is enabled
                        Log.d("MyTag", "GPS is enabled");
                        // Handle the GPS enabled state here
                    } else {
                        // GPS is disabled
                        Log.d("MyTag", "GPS is disabled");
                        // Handle the GPS disabled state here
                    }
                }
            }
        }

        if (intent.getAction() != null && intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            // Location provider status has changed (GPS turned on/off)
            boolean isLocationEnabled = isLocationEnabled(context);
            if (!isLocationEnabled) {
                Toast.makeText(context, "Service Stopped", Toast.LENGTH_SHORT).show();
                context.stopService(new Intent(context, GeofenceService.class));
            }
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d("MyTag", "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d("MyTag", "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                break;
        }

    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction() != null) {
////            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
////                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
////
////                if (locationManager != null) {
////                    boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
////                    if (isGpsEnabled) {
////                        // GPS signal is available, start the GeofenceService
////                        Log.d("GpsBroadcastReceiver", "GPS signal available, starting GeofenceService");
//////                        context.startService(new Intent(context, GeofenceService.class));
////                    } else {
////                        // GPS signal is not available, stop the GeofenceService
////                        Log.d("GpsBroadcastReceiver", "GPS signal not available, stopping GeofenceService");
////                        Toast.makeText(context, "Service Stopped", Toast.LENGTH_SHORT).show();
////                        context.stopService(new Intent(context, GeofenceService.class));
////                    }
////                }
////            }
////
////        }
//    }
    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
