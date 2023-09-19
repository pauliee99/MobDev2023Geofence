package com.example.mobdev2023geofence;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeofenceService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private ContentResolver resolver;
    private AppDatabase db;
    private CircleDAO circleDAO;
    private SessionDAO sessionDAO;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 50; // 50 meters
    private static final long MIN_TIME_BETWEEN_UPDATE = 5000; // 5 seconds
    private CurrentSession currentSession;
    public boolean isPaused = false;
    private GpsBroadcastReceiver gpsBroadcastReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        gpsBroadcastReceiver = new GpsBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(gpsBroadcastReceiver, intentFilter);

        resolver = getContentResolver();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "circles")
                .fallbackToDestructiveMigration()
                .build();
        circleDAO = db.circleDAO();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Check if the location has changed by more than 50 meters
                if (!isPaused) {
                    if (isLocationChanged(location)) {
                        checkGeofence(location);
                    }
                }
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // Handle provider disabled
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // Handle provider enabled
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Request location updates
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BETWEEN_UPDATE,
                    MIN_DISTANCE_CHANGE_FOR_UPDATE,
                    locationListener
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
        // Stop location updates when the service is destroyed
        locationManager.removeUpdates(locationListener);

        if (gpsBroadcastReceiver != null) {
            unregisterReceiver(gpsBroadcastReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isLocationChanged(Location newLocation) {
        // Implement your logic to check if the location has changed by more than 50 meters
        // For example, you can compare it with the previous location.
        // You may need to store the previous location in a variable.
        // Return true if location has changed, false otherwise.
        return false;
    }

    private void checkGeofence(Location currentLocation) {
        // Retrieve the list of circles from the database using the Content Provider
        Uri uri = Uri.parse("content://com.example.mobdev2023geofence/circle");
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "circles").build();
        List<CircleEntryPoint> circleEntryPoints = new ArrayList<>();
        CircleEntryPointsDAO circleEntryPointsDAO = db.circleEntryPointsDAO();

        new Thread(() -> {
            List<Circle> circles = (List<Circle>) circleDAO.getLastSessionCircles(sessionDAO.getLastSessionId());
            for (Circle circle : circles) {
                LatLng circleCenter = new LatLng(circle.latitude, circle.longitude);
                double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        circleCenter.latitude, circleCenter.longitude);

                // Check if the distance is within the geofence radius
                if (distance <= 100) {
                    CircleEntryPoint circleEntryPoint = new CircleEntryPoint();
                    circleEntryPoint.latitude = circleCenter.latitude;
                    circleEntryPoint.longitude = circleCenter.longitude;
                    circleEntryPoint.sessionId = currentSession.getCurrentSessionId();
                    circleEntryPointsDAO.insertCircleEntryPoints(circleEntryPoint);
                    Log.d("GeofenceService", "Entered geofence: " + circle.id);
                    Toast.makeText(this, "Entered geofence: "+ circle.id, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Helper method to calculate distance between two sets of latitude and longitude
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Use the Haversine formula to calculate distance
        // Implement the formula as described in the provided link:
        // https://www.movable-type.co.uk/scripts/latlong.html

        double R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Distance in meters
    }

    public void togglePause() {
        isPaused = !isPaused;
    }
}
