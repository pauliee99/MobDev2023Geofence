package com.example.mobdev2023geofence;

import static java.sql.Types.NULL;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobdev2023geofence.databinding.ActivityMapsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseHelper helper;

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
        ArrayList<Circle> circles = new ArrayList<>();

        helper = new DatabaseHelper(MapsActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        if (database != null) {
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
            if(cursor.moveToFirst()){
                do{
                    CircleOptions circleOptions = new CircleOptions();
                    LatLng latlng = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                    circleOptions.center(latlng);
                    circleOptions.radius(100);
                    circleOptions.strokeColor(Color.RED);
                    circleOptions.fillColor(0x11FFA420);
                    circleOptions.visible(true);
                    Circle newCircle = mMap.addCircle(circleOptions);
                    circles.add(newCircle);
                }while(cursor.moveToNext());
            }
        }

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
                    String selection = DatabaseHelper.FIELD_1 + " = ? AND " + DatabaseHelper.FIELD_2 + " = ?";
                    String[] selectionArgs = {String.valueOf(existingCircle.getCenter().latitude), String.valueOf(existingCircle.getCenter().longitude)};

                    Cursor cursor = database.query(
                            DatabaseHelper.TABLE_NAME,
                            null, // You can specify the columns you want to retrieve here, or use null for all columns
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );
                    boolean existsInDatabase = cursor.moveToFirst();

                    if (existsInDatabase) {
                        database.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);
                    } else {
                        // The combination does not exist in the database, handle it accordingly
                    }
                    cursor.close();
                    break;
                }
            }

            if (circleToRemove != null) {
                circleToRemove.remove();
                circles.remove(circleToRemove);
            } else {
                Circle newCircle = mMap.addCircle(circleOptions);
                circles.add(newCircle);
            }
        });

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < circles.size(); i++){
                    double lat = circles.get(i).getCenter().latitude;
                    double lon = circles.get(i).getCenter().longitude;
                    double rad = circles.get(i).getRadius();
                    int fill = circles.get(i).getFillColor();
                    int stroke = circles.get(i).getStrokeColor();
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.FIELD_1, lat);
                    values.put(DatabaseHelper.FIELD_2, lon);
                    values.put(DatabaseHelper.FIELD_3, rad);
                    values.put(DatabaseHelper.FIELD_4, fill);
                    values.put(DatabaseHelper.FIELD_5, stroke);

                    SQLiteDatabase database = helper.getWritableDatabase();
                    database.insert(DatabaseHelper.TABLE_NAME, null, values);
                }
            }
        });

        FloatingActionButton cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MapsActivity.this, MainActivity.class));
                circles.clear();
                Toast.makeText(MapsActivity.this, "changes discarded", Toast.LENGTH_SHORT).show();
                database.close();
                finish();
            }
        });

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}