package com.example.mobdev2023geofence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Objects;

public class MapActivitytmp extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private final static int FINE_LOCATION_PERMISSION_REQUEST = 4;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapactivity);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocation();
    }

    private void requestLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case FINE_LOCATION_PERMISSION_REQUEST:
                for (int i=0; i<=permissions.length; i++){
                    String permission = permissions[i];
                    if(Objects.equals(permission, Manifest.permission.ACCESS_FINE_LOCATION)){
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            requestLocation();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LocationListener", location.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }
}