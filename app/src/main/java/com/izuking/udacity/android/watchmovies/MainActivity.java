package com.izuking.udacity.android.watchmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.izuking.udacity.android.watchmovies.DbUtil.MovieContract;
import com.izuking.udacity.android.watchmovies.utils.JSONObjects;
import com.izuking.udacity.android.watchmovies.utils.MoviePreference;
import com.izuking.udacity.android.watchmovies.utils.NetworkUtils;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASK_LOADER_ID = 26;

    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView errMessage;
    private ActionBar actionBar;

    public static ArrayList<String> convertStringToList(String string) {

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> list = new Gson().fromJson(string, type);
        return list;

    }

    public static String convertListToString(ArrayList<String> string) {

        return new Gson().toJson(string);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();

        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_main);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        errMessage = (TextView) findViewById(R.id.tv_error_message);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        if (savedInstanceState == null)
            this.movieInit(MoviePreference.getSortOrder(this));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void movieInit(String sortType) {

        toggleActionBar(sortType);
        if (sortType.equals(getString(R.string.action_favorite))) {
            movieAdapter = new MovieAdapter(this, this, MovieAdapter.UseCase.FROM_DATABASE);
            recyclerView.setAdapter(movieAdapter);
            showMovieView();

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<Cursor> loader = loaderManager.getLoader(TASK_LOADER_ID);

            if (loader == null)
                getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
            else
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        } else {
            movieAdapter = new MovieAdapter(this, this, MovieAdapter.UseCase.FROM_API);
            recyclerView.setAdapter(movieAdapter);
            showMovieView();

            new FetchMovie().execute(getString(R.string.api_key), sortType);
        }

    }

    private void toggleActionBar(String sortType) {

        if (sortType.equals(getString(R.string.popular)))
            actionBar.setTitle(getString(R.string.action_popular));

        else if (sortType.equals(getString(R.string.top_rated)))
            actionBar.setTitle(getString(R.string.action_toprated));

        else if (sortType.equals(getString(R.string.action_favorite)))
            actionBar.setTitle(getString(R.string.action_favorite));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action actionBar item clicks here. The action actionBar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.nav_refresh) {
            String sortType = MoviePreference.getSortOrder(this);
            movieInit(sortType);
        } else if (id == R.id.nav_popular) {
            MoviePreference.setSortOrder(this, getString(R.string.popular));
            movieInit(getString(R.string.popular));
        } else if (id == R.id.nav_toprated) {
            MoviePreference.setSortOrder(this, getString(R.string.top_rated));
            movieInit(getString(R.string.top_rated));
        } else if (id == R.id.nav_favorite) {
            MoviePreference.setSortOrder(this, getString(R.string.action_favorite));
            movieInit(getString(R.string.action_favorite));
        }

        return true;
    }

    @Override
    public void onclick(JSONObjects.MovieModel movieInfo, int position) {

        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra(getString(R.string.title), movieInfo.getOriginalTitle());
        intent.putExtra(getString(R.string.movie_img), movieInfo.getPosterPath());
        intent.putExtra(getString(R.string.movie_overview), movieInfo.getOverview());
        intent.putExtra(getString(R.string.rating), movieInfo.getVoteAverage());
        intent.putExtra(getString(R.string.trailer_id), movieInfo.getTrailerId());
        intent.putExtra(getString(R.string.releaseDate), movieInfo.getReleaseDate());
        intent.putExtra(getString(R.string.trailer_reviews_author), movieInfo.getReviewAuthors());
        intent.putExtra(getString(R.string.trailer_reviews_content), movieInfo.getReviewContents());
        intent.putExtra(getString(R.string.movie_trailer_keys), movieInfo.getTrailerYoutubeKeys());

        startActivity(intent);

    }

    private void showErrorView(String msg) {
        recyclerView.setVisibility(View.INVISIBLE);
        errMessage.setVisibility(View.VISIBLE);
        errMessage.setText(msg);

    }

    private void showMovieView() {
        errMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor cursor = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                mLoadingIndicator.setVisibility(View.VISIBLE);
                if (cursor != null)
                    deliverResult(cursor);
                else
                    forceLoad();
            }

            @Override
            public Cursor loadInBackground() {

                return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                cursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data.getCount() > 0) {
            showMovieView();
            movieAdapter.setMovieData(new JSONObjects().extractData(data));
        } else {
            showErrorView(getString(R.string.error_msg_database));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.movieInit(MoviePreference.getSortOrder(this));
    }

    public class FetchMovie extends AsyncTask<String, Void, List<JSONObjects.MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<JSONObjects.MovieModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String apiKey = params[0];
            String sortOrder = params[1];

            URL movieRequestURL = NetworkUtils.buildUrl(sortOrder, apiKey);

            try {
                String movieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestURL);
                List<JSONObjects.MovieModel> movieModels = new JSONObjects().getMovieList(movieResponse, apiKey);

                return movieModels;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<JSONObjects.MovieModel> responseList) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (responseList != null) {
                showMovieView();

                movieAdapter.setMovieData(responseList);
            } else {
                showErrorView(getString(R.string.error_msg_api));
            }
        }

    }


}

