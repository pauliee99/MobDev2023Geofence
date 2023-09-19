package com.example.mobdev2023geofence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class Session {
    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name="start_time")
    public long startTime;

    @ColumnInfo(name="end_time")
    public long endTime;
}
