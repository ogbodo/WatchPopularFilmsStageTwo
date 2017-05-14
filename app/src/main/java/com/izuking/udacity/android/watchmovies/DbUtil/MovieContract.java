package com.izuking.udacity.android.watchmovies.DbUtil;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Izuking on 4/30/2017.
 */

public class MovieContract {
    public static final String PATH_TABLE = "favorites";
    public static final String AUTHORITY = "com.izuking.udacity.android.watchmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TABLE).build();

        public static final String TABLENAME = "favorite";
        public static final String ORIGINALTITLE = "original_title";
        public static final String POSTERPATH = "poster_path";
        public static final String OVERVIEW = "overview";
        public static final String REVIEWCONTENT = "content";
        public static final String REVIEWAUTHOR = "author";
        public static final String VOTEAVERAGE = "vote_average";
        public static final String RELEASEDATE = "release_date";
        public static final String TRAILERID = "movie_id";
        public static final String MOVIE_TRAILERS_KEY = "trailerYoutubeKeys";

    }
}
