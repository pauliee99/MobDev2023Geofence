package com.example.mobdev2023geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

public class GpsBroadcastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                if (locationManager != null) {
                    boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (isGpsEnabled) {
                        // GPS signal is available, start the GeofenceService
                        Log.d("GpsBroadcastReceiver", "GPS signal available, starting GeofenceService");
//                        context.startService(new Intent(context, GeofenceService.class));
                    } else {
                        // GPS signal is not available, stop the GeofenceService
                        Log.d("GpsBroadcastReceiver", "GPS signal not available, stopping GeofenceService");
                        Toast.makeText(context, "Service Stopped", Toast.LENGTH_SHORT).show();
                        context.stopService(new Intent(context, GeofenceService.class));
                    }
                }
            }

        }
    }

}
