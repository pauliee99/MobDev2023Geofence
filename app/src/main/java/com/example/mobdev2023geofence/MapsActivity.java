package com.example.mobdev2023geofence;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.mobdev2023geofence.databinding.ActivityMapsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static String AUTHORITY = "com.example.mobdev2023geofence";
    private static String PATH = "circle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 4) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showPosition();
            }
        }
    }

    private void showPosition(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);
        showPosition();
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng()));
        List<Circle> circles = new ArrayList<>();
        List coordinates = new ArrayList();

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "circles").build();
        CircleDAO circleDAO = db.circleDAO();
        ContentResolver resolver = this.getContentResolver();
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            List<com.example.mobdev2023geofence.Circle> circlesdb = circleDAO.getAll();
            circlesdb.forEach((tmp)->{
                Log.d("MyTag", "Circle ID: " + tmp.id + ", Latitude: " + tmp.latitude + ", Longitude: " + tmp.longitude);
                LatLng latlng = new LatLng(tmp.latitude, tmp.longitude);
                coordinates.add(latlng);
            });
            latch.countDown();
        }).start();

        try {
            // Wait for the thread to finish using the CountDownLatch
            latch.await();

            // You can now safely access 'circlesdb' here, and it should contain the fetched data
            coordinates.forEach((tmp)->{
                Log.d("MyTag", "Circle ID: " + (LatLng) tmp);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        coordinates.forEach((tmp) -> {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center((LatLng) tmp);
            circleOptions.radius(100);
            circleOptions.strokeColor(Color.RED);
            circleOptions.fillColor(0x11FFA420);
            circleOptions.visible(true);
            Circle newCircle = mMap.addCircle(circleOptions);
            circles.add(newCircle);
        });

        // on map click
        mMap.setOnMapLongClickListener(latLng -> {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(100);
            circleOptions.strokeColor(Color.RED);
            circleOptions.fillColor(0x11FFA420);
            circleOptions.visible(true);
            Circle circleToRemove = null;
//            Circle newCircle = mMap.addCircle(circleOptions);
//            circles.add(newCircle);

            for (Circle existingCircle : circles) {
                LatLng center = existingCircle.getCenter();
                double distance = SphericalUtil.computeDistanceBetween(center, latLng);

                if (distance < existingCircle.getRadius()) {
                    circleToRemove = existingCircle; //remove circle form the list
                    // and also remove it form the database if it exists
                    if (circleToRemove == null){
                        Log.d("MyTag", "circle is null");
                    }
                    LatLng centerToRemove = circleToRemove.getCenter();

                    new Thread(() -> {
                        double latitude = centerToRemove.latitude;
                        double longitude = centerToRemove.longitude;
                        int deletedRows = circleDAO.deleteCircleByLatLon(centerToRemove.latitude, centerToRemove.longitude);
                        if (deletedRows > 0) {
                            Logger.getAnonymousLogger().severe("record deleted successfully");
                        } else {
                            Logger.getAnonymousLogger().severe("record not found");
                        }
                    }).start();
                    circleToRemove.remove();
                    circles.remove(circleToRemove);
                    break;
                }
            }

            if (circleToRemove != null) {
//                circleToRemove.remove();
//                circles.remove(circleToRemove);
            } else {
                Circle newCircle = mMap.addCircle(circleOptions);
                circles.add(newCircle);
            }
        });

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() { // start service button
            @Override
            public void onClick(View view) {
                for (int i = 0; i < circles.size(); i++){
                    com.example.mobdev2023geofence.Circle circle = new com.example.mobdev2023geofence.Circle();
                    circle.latitude = circles.get(i).getCenter().latitude;
                    circle.longitude = circles.get(i).getCenter().longitude;
                    List<com.example.mobdev2023geofence.Circle> qwe = new ArrayList<>();
                    new Thread(() ->circleDAO.insertCircle(circle)).start();
                }
                startGeofenceService();
                Toast.makeText(MapsActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() { // go back button
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MapsActivity.this, MainActivity.class));
                circles.clear();
                Toast.makeText(MapsActivity.this, "changes discarded", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void startGeofenceService() {
        Intent serviceIntent = new Intent(this, GeofenceService.class);
        startService(serviceIntent);
    }


}