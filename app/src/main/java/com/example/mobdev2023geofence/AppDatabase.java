package com.example.mobdev2023geofence;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Circle.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract CircleDAO circleDAO();
}
