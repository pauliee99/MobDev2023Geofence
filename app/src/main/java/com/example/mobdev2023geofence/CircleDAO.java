package com.example.mobdev2023geofence;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CircleDAO {
    @Query("SELECT * FROM circles")
    public List<Circle> getAll();

    @Query("SELECT * FROM circles")
    public Cursor getCursorAll();

    @Query("SELECT * FROM circles WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    public Circle getCircleUnique(double lat, double lon);

    @Query("SELECT * FROM circles WHERE id = :id")
    public Circle getCircleById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertCircle(Circle... circles);

    @Query("DELETE FROM circles WHERE latitude = :lat AND longitude = :lon")
    int deleteCircleByLatLon(double lat, double lon);
}
