package com.example.mobdev2023geofence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "circles", indices = {@Index(value = {"latitude", "longitude"}, unique = true)})
public class Circle {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="latitude")
    public  double latitude;

    @ColumnInfo(name="longitude")
    public  double longitude;

}
