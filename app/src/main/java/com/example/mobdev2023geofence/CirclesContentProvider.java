package com.example.mobdev2023geofence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class CirclesContentProvider extends ContentProvider {
    private UriMatcher uriMatcher;
    private static String AUTHORITY = "com.example.mobdev2023geofence";
    private static String PATH = "circle";
    private CircleDAO circleDAO;


    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,PATH,1);
        uriMatcher.addURI(AUTHORITY,PATH+"/#", 2);

        AppDatabase db = Room.databaseBuilder(getContext(),AppDatabase.class, "circles")
                .fallbackToDestructiveMigration() // recreate database if version changes.
                .build();
        circleDAO = db.circleDAO();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(new String[] {"id", "latitude", "longitude"});
//        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case 1:
                List<Circle> circles = circleDAO.getAll();
                for (Circle circle:circles){
                    cursor.newRow()
                            .add("id", circle.id)
                            .add("latitude", circle.latitude)
                            .add("longitude", circle.longitude);
                }
                //cursor = circleDAO.getCursorAll();
                break;
            case 2:
                int id = Integer.parseInt(uri.getLastPathSegment());
                Circle circle = circleDAO.getCircleById(id);
                cursor.newRow()
                        .add("id", circle.id)
                        .add("latitude", circle.latitude)
                        .add("longitude", circle.longitude);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case 2:
                // Get the latitude and longitude values from the URI
                String latitudeStr = uri.getQueryParameter("latitude");
                String longitudeStr = uri.getQueryParameter("longitude");

                if (latitudeStr != null && longitudeStr != null) {
                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);

                    // Perform the deletion using your DAO by latitude and longitude
                    deletedRows = circleDAO.deleteCircleByLatLon(latitude, longitude);
                } else {
                    throw new IllegalArgumentException("Latitude and Longitude parameters are required.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify observers that the data has changed (if necessary)
        getContext().getContentResolver().notifyChange(uri, null);

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
