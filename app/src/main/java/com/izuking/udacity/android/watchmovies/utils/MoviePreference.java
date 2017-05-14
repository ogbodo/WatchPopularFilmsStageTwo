package com.izuking.udacity.android.watchmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.izuking.udacity.android.watchmovies.R;


/**
 * Created by Izuking on 4/14/2017.
 */
public class MoviePreference {

    public static String getSortOrder(Context cxt) {
        SharedPreferences shared = cxt.getSharedPreferences(cxt.getString(R.string.preference), Context.MODE_PRIVATE);
        return shared.getString(cxt.getString(R.string.sort_type), cxt.getString(R.string.popular));
    }

    public static void setSortOrder(Context cxt, String sortType) {
        SharedPreferences shared = cxt.getSharedPreferences(cxt.getString(R.string.preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(cxt.getString(R.string.sort_type), sortType);
        editor.commit();
    }


}
