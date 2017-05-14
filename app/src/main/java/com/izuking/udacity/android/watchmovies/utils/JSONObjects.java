package com.izuking.udacity.android.watchmovies.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.izuking.udacity.android.watchmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.MOVIE_TRAILERS_KEY;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.ORIGINALTITLE;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.OVERVIEW;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.POSTERPATH;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.RELEASEDATE;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.REVIEWAUTHOR;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.REVIEWCONTENT;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.TRAILERID;
import static com.izuking.udacity.android.watchmovies.DbUtil.MovieContract.MovieEntry.VOTEAVERAGE;
import static com.izuking.udacity.android.watchmovies.MainActivity.convertStringToList;

/**
 * Created by Izuking on 4/14/2017.
 */
public class JSONObjects {
    private static final String RESULT = "results";
    private static final String ID = "id";
    private static final String TRAILERKEY = "key";


    public List<MovieModel> getMovieList(String jsonResponse, String apiKey) throws JSONException, IOException {
        JSONObject object = new JSONObject(jsonResponse);
        JSONArray data = object.getJSONArray(RESULT);
        List<MovieModel> movieModels = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            MovieModel movieModel = new MovieModel();

            JSONObject movieInfo = (JSONObject) data.get(i);

            movieModel.setTrailerId(Integer.toString(movieInfo.getInt(ID)));
            movieModel.setOriginalTitle(movieInfo.getString(ORIGINALTITLE));
            movieModel.setOverview(movieInfo.getString(OVERVIEW));
            movieModel.setPosterPath(movieInfo.getString(POSTERPATH));
            movieModel.setReleaseDate(movieInfo.getString(RELEASEDATE));
            movieModel.setVoteAverage(movieInfo.getString(VOTEAVERAGE));


            JSONArray retrievedTrailerJsonArray = retrieveItems(movieModel.getTrailerId() + "/videos", apiKey);
            ArrayList<String> retrievedTrailerList = new ArrayList<>();

            for (int j = 0; j < retrievedTrailerJsonArray.length(); j++) {

                JSONObject itemInfo = (JSONObject) retrievedTrailerJsonArray.get(j);
                retrievedTrailerList.add(itemInfo.getString(TRAILERKEY));

            }
            movieModel.setTrailerYoutubeKeys(retrievedTrailerList);


            JSONArray retrievedReviewJsonArray = retrieveItems(movieModel.getTrailerId() + "/reviews", apiKey);
            ArrayList<String> retrievedReviewAuthorList = new ArrayList<>();
            ArrayList<String> retrievedReviewContentList = new ArrayList<>();

            for (int j = 0; j < retrievedReviewJsonArray.length(); j++) {

                JSONObject itemInfo = (JSONObject) retrievedReviewJsonArray.get(j);
                retrievedReviewAuthorList.add(itemInfo.getString(REVIEWAUTHOR));
                retrievedReviewContentList.add(itemInfo.getString(REVIEWCONTENT));


            }
            movieModel.setReviewAuthors(retrievedReviewAuthorList);
            movieModel.setReviewContents(retrievedReviewContentList);


            movieModels.add(movieModel);

        }

        return movieModels;
    }

    private JSONArray retrieveItems(String path, String apiKey) throws IOException, JSONException {
        URL trailerURL = NetworkUtils.buildUrl(path, apiKey);
        String trailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerURL);
        JSONObject retrievedItemObjects = new JSONObject(trailerResponse);

        return retrievedItemObjects.getJSONArray(RESULT);
    }


    public List<JSONObjects.MovieModel> extractData(Cursor data) {
        List<JSONObjects.MovieModel> movieModels = new ArrayList<>();

        while (data.moveToNext()) {
            MovieModel movieModel = new MovieModel();
            movieModel.setOriginalTitle(data.getString(data.getColumnIndex(ORIGINALTITLE)));
            movieModel.setPosterPath(data.getString(data.getColumnIndex(POSTERPATH)));
            movieModel.setOverview(data.getString(data.getColumnIndex(OVERVIEW)));
            movieModel.setVoteAverage(data.getString(data.getColumnIndex(VOTEAVERAGE)));
            movieModel.setReleaseDate(data.getString(data.getColumnIndex(RELEASEDATE)));
            movieModel.setTrailerId(data.getString(data.getColumnIndex(TRAILERID)));
            movieModel.setReviewAuthors(convertStringToList(data.getString(data.getColumnIndex(REVIEWAUTHOR))));
            movieModel.setReviewContents(convertStringToList(data.getString(data.getColumnIndex(REVIEWCONTENT))));

            movieModel.setTrailerYoutubeKeys(convertStringToList(data.getString(data.getColumnIndex(MOVIE_TRAILERS_KEY))));

            movieModels.add(movieModel);
        }
        return movieModels;
    }

    public MovieModel extractData(Context context, Intent intent) {
        MovieModel movieModel = new MovieModel();

        movieModel.setOriginalTitle(intent.getStringExtra(context.getString(R.string.title)));
        movieModel.setPosterPath(intent.getStringExtra(context.getString(R.string.movie_img)));
        movieModel.setReleaseDate(intent.getStringExtra(context.getString(R.string.releaseDate)));
        movieModel.setVoteAverage(intent.getStringExtra(context.getString(R.string.rating)));
        movieModel.setOverview(intent.getStringExtra(context.getString(R.string.movie_overview)));
        movieModel.setTrailerYoutubeKeys(intent.getStringArrayListExtra(context.getString(R.string.movie_trailer_keys)));
        movieModel.setReviewAuthors(intent.getStringArrayListExtra(context.getString(R.string.trailer_reviews_author)));
        movieModel.setReviewContents(intent.getStringArrayListExtra(context.getString(R.string.trailer_reviews_content)));
        movieModel.setTrailerId(intent.getStringExtra(context.getString(R.string.trailer_id)));
        return movieModel;
    }

    public class MovieModel {
        private String originalTitle;
        private String posterPath;
        private String overview;
        private String voteAverage;
        private String releaseDate;
        private String trailerId;
        private ArrayList<String> trailerYoutubeKeys;
        private ArrayList<String> reviewContents;
        private ArrayList<String> reviewAuthors;

        public ArrayList<String> getReviewAuthors() {
            return reviewAuthors;
        }

        public void setReviewAuthors(ArrayList<String> reviewAuthors) {
            this.reviewAuthors = reviewAuthors;
        }

        public ArrayList<String> getReviewContents() {
            return reviewContents;
        }

        public void setReviewContents(ArrayList<String> reviewContents) {
            this.reviewContents = reviewContents;
        }

        public String getOriginalTitle() {
            return originalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(String voteAverage) {
            this.voteAverage = voteAverage;
        }

        public String getTrailerId() {
            return trailerId;
        }

        public void setTrailerId(String trailerId) {
            this.trailerId = trailerId;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public ArrayList<String> getTrailerYoutubeKeys() {
            return trailerYoutubeKeys;
        }

        public void setTrailerYoutubeKeys(ArrayList<String> trailerYoutubeKeys) {
            this.trailerYoutubeKeys = trailerYoutubeKeys;
        }
    }
}
