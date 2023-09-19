package com.example.mobdev2023geofence;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobdev2023geofence.databinding.ActivityResultsMapBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ResultsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityResultsMapBinding binding;
    GeofenceService geofenceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultsMapBinding.inflate(getLayoutInflater());
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
        showPosition();
        List<com.google.android.gms.maps.model.Circle> circles = new ArrayList<>();
        List coordinates = new ArrayList();
        List points = new ArrayList();
        CurrentSession currentSession = (CurrentSession) getApplication();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "circles").build();
        CircleDAO circleDAO = db.circleDAO();
        SessionDAO sessionDAO = db.sessionDAO();
        CircleEntryPointsDAO circleEntryPointsDAO = db.circleEntryPointsDAO();
        final CountDownLatch latch = new CountDownLatch(1);

        // Add a marker in Sydney and move the camera
        LatLng athens = new LatLng(37, 23);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));


        new Thread(() -> {
            try {
                List<Circle> circlesdb = null;
                if (currentSession.getCurrentSessionId() != null) {
                    circlesdb = circleDAO.getLastSessionCircles(currentSession.getCurrentSessionId());
                } else {
                    circlesdb = circleDAO.getLastSessionCircles(sessionDAO.getLastSessionId());
                }
                circlesdb.forEach((tmp) -> {
                    Log.d("MyTag", "Circle ID: " + tmp.id + ", Latitude: " + tmp.latitude + ", Longitude: " + tmp.longitude);
                    LatLng latlng = new LatLng(tmp.latitude, tmp.longitude);
                    coordinates.add(latlng);
                });
                List<CircleEntryPoint> circleEntryPoints = circleEntryPointsDAO.getLastSession(currentSession.getCurrentSessionId());
                circleEntryPoints.forEach((tmp) -> {
                    Log.d("MyTag", "fence ID: " + tmp.id + ", Latitude: " + tmp.latitude + ", Longitude: " + tmp.longitude);
                    LatLng latlng = new LatLng(tmp.latitude, tmp.longitude);
                    points.add(latlng);
                });
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("MyTag", "Accessing data from db failed because:" + e.getMessage());
            }
        }).start();

        try {
            //wait for the thread to finish taking coordnates from the db
            latch.await();
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
            mMap.addCircle(circleOptions);
//            com.google.android.gms.maps.model.Circle newCircle = mMap.addCircle(circleOptions);
//            circles.add(newCircle);
        });
        points.forEach((tmp) -> {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center((LatLng) tmp);
            circleOptions.radius(5);
            circleOptions.strokeColor(Color.BLUE);
            circleOptions.fillColor(0x11FFA420);
            circleOptions.visible(true);
            mMap.addCircle(circleOptions);
//            com.google.android.gms.maps.model.Circle newCircle = mMap.addCircle(circleOptions);
        });

        FloatingActionButton cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() { // go back button
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (geofenceService != null) {
                    geofenceService.togglePause();

                    if (geofenceService.isPaused) {
                        pauseButton.setText("Resume");
                    } else {
                        pauseButton.setText("Pause");
                    }
                }
            }
        });
    }
}