package com.example.mobdev2023geofence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CircleEntryPointsDAO {

    @Query("SELECT * FROM circle_entry_points")
    public List<CircleEntryPoint> getAll();

    @Query("SELECT * FROM circle_entry_points WHERE session_id = :sessionId")
    public List<CircleEntryPoint> getLastSession(String sessionId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertCircleEntryPoints(CircleEntryPoint... circleEntryPoints);
}
