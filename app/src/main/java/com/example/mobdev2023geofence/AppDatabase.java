package com.example.mobdev2023geofence;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Circle.class, Session.class, CircleEntryPoint.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract CircleDAO circleDAO();
    public abstract SessionDAO sessionDAO();
    public abstract CircleEntryPointsDAO circleEntryPointsDAO();
}
