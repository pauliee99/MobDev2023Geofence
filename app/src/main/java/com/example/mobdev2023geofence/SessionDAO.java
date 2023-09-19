package com.example.mobdev2023geofence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SessionDAO {

    @Query("SELECT * FROM sessions")
    public List<Session> getAll();

    @Insert
    public void insertSession(Session... session);

    @Update
    public void updateSession(Session session);

    @Query("UPDATE sessions SET end_time = :endTime WHERE end_time = 0")
    public void updateSessionEndTime(long endTime);

    @Query("SELECT id FROM sessions WHERE end_time = 0")
    public List<String> getCurrentSessionId();

    @Delete
    public void deleteSession(Session session);

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    public Session getSessionById(long sessionId);

    @Query("SELECT id FROM sessions ORDER BY start_time DESC LIMIT 1")
    public String getLastSessionId();
}
