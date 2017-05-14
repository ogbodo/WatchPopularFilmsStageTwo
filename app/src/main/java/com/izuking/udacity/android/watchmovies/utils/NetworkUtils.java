package com.izuking.udacity.android.watchmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Izuking on 4/14/2017.
 */
public class NetworkUtils {

    public static final String IMGURL = "http://image.tmdb.org/t/p/w185/";
    private static final String BASEURL = "https://api.themoviedb.org/3/movie/";
    private static final String QUERY = "api_key";


    public static URL buildUrl(String path, String apikey) {
        Uri builtUri = Uri.parse(BASEURL).buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(QUERY, apikey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
