package com.izuking.udacity.android.watchmovies.DbUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.MOVIE_TRAILERS_KEY;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.ORIGINALTITLE;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.OVERVIEW;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.POSTERPATH;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.RELEASEDATE;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.REVIEWAUTHOR;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.REVIEWCONTENT;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.TABLENAME;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.TRAILERID;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.VOTEAVERAGE;

/**
 * Created by Izuking on 4/30/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DATABASE_FAVORITE_TRAILERS_TABLE = "CREATE TABLE " + TABLENAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORIGINALTITLE + " TEXT NOT NULL, " +
                POSTERPATH + " TEXT NOT NULL," +
                OVERVIEW + " TEXT NOT NULL, " +
                REVIEWCONTENT + " TEXT NOT NULL, " +
                REVIEWAUTHOR + " TEXT NOT NULL, " +
                VOTEAVERAGE + " TEXT NOT NULL, " +
                RELEASEDATE + " TEXT NOT NULL, " +
                TRAILERID + " TEXT UNIQUE NOT NULL, " +
                MOVIE_TRAILERS_KEY + " TEXT NOT NULL" + "); ";

        db.execSQL(SQL_CREATE_DATABASE_FAVORITE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }
}
