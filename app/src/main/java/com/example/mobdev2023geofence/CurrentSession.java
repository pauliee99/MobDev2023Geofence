package com.example.mobdev2023geofence;

import android.app.Application;

public class CurrentSession extends Application {
    private String currentSessionId;

    public String getCurrentSessionId() {return currentSessionId;}
    public void setCurrentSessionId(String sessionId) {this.currentSessionId = sessionId;}
}
