package com.example.mobdev2023geofence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "CIRCLES_DB";
    private static int DB_VERSION = 1;
    public static String TABLE_NAME = "CIRCLES";
    public static String FIELD_1 = "LATITUDE";
    public static String FIELD_2 = "LONGITUDE";
    public static String FIELD_3 = "RADIUS";
    public static String FIELD_4 = "FILL_COLOR";
    public static String FIELD_5 = "STROKE_COLOR";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUERY = "CREATE TABLE " +
                CirclesObject.TABLE_NAME + " (" +
                CirclesObject.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CirclesObject.LATITUDE + " REAL NOT NULL, " +
                CirclesObject.LONGITUDE + " REAL NOT NULL, " +
                CirclesObject.RADIUS + " REAL NOT NULL, " +
                CirclesObject.FILL_COLOR + " INTEGER NOT NULL, " +
                CirclesObject.STROKE_COLOR + " INTEGER NOT NULL);";
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
