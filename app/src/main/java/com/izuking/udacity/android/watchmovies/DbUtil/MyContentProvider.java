package com.izuking.udacity.android.watchmovies.DbUtil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.AUTHORITY;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.TABLENAME;

public class MyContentProvider extends ContentProvider {
    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITE = 101;
    private static final UriMatcher uriMatcher = buildURIMatcher();

    private DatabaseHandler databaseHandler;

    private static UriMatcher buildURIMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, MovieContract.PATH_TABLE, CODE_FAVORITES);
        uriMatcher.addURI(AUTHORITY, MovieContract.PATH_TABLE + "/*", CODE_FAVORITE);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        databaseHandler = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int rows;
        switch (match) {
            case CODE_FAVORITES:

                rows = db.delete(TABLENAME, selection, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = databaseHandler.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri uri1 = null;

        switch (match) {
            case CODE_FAVORITES:

                long id = db.insert(TABLENAME, null, values);

                if (id > 0)
                    uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                else
                    throw new SQLException("Unable to insert data into the database!");

                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return uri1;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = databaseHandler.getReadableDatabase();

        int match = uriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case CODE_FAVORITES:

                cursor = db.query(TABLENAME, projection, selection, selectionArgs, null, null, MovieContract.MovieEntry._ID);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
