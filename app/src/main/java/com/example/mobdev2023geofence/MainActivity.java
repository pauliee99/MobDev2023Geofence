package com.example.mobdev2023geofence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long sessionStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startSession();

        // @TODO: maybe add some progress bars around here ( MobDev (2022 12 12) )
        Button btnMapActivity = (Button)findViewById(R.id.buttonMapActivity);
        Button btnResultsMapActivity = (Button)findViewById(R.id.buttonResultsMapActivity);
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
    }

    private void startSession() {
        sessionStartTime = System.currentTimeMillis();
        // Display a welcome message or perform any other actions.
        Toast.makeText(this, "Welcome! Session started.", Toast.LENGTH_SHORT).show();
    }

    private void endSession() {
        long sessionEndTime = System.currentTimeMillis();
        long sessionDuration = sessionEndTime - sessionStartTime;

        // Record session data, e.g., in a database or log file
        Log.d("SessionInfo", "Session started at: " + sessionStartTime);
        Log.d("SessionInfo", "Session ended at: " + sessionEndTime);
        Log.d("SessionInfo", "Session duration (ms): " + sessionDuration);

        // Display a message to indicate the session has ended
        Toast.makeText(this, "Session ended.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // End the session when the app is closed
        endSession();
    }
}