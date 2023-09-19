package com.example.mobdev2023geofence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "circle_entry_points")
public class CircleEntryPoint {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="latitude")
    public double latitude;

    @ColumnInfo(name="longitude")
    public double longitude;

    @ColumnInfo(name="session_id")
    public String sessionId;
}
