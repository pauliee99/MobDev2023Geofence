package com.example.mobdev2023geofence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // @TODO: maybe add some progress bars around here ( MobDev (2022 12 12) )
        Button btnMapActivity = (Button)findViewById(R.id.buttonMapActivity);
        Button btnResultsMapActivity = (Button)findViewById(R.id.buttonResultsMapActivity);
        Button btnStopService = (Button)findViewById(R.id.btnStopService);
        btnMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        btnResultsMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ResultsMapActivity.class));
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSession();
                Intent serviceIntent = new Intent(MainActivity.this, GeofenceService.class);
                stopService(serviceIntent);
            }
        });
    }

    private void endSession() {
        try {
            long endTime = System.currentTimeMillis();
            final CountDownLatch latch = new CountDownLatch(1);

            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "circles").build();
            SessionDAO userSessionDao = db.sessionDAO();
            new Thread(() -> {
                userSessionDao.updateSessionEndTime(endTime);
                latch.countDown();
            }).start();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyTag", "Session end failed: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}