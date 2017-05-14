package com.izuking.udacity.android.watchmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.izuking.udacity.android.watchmovies.DbUtil.MovieContract;
import com.izuking.udacity.android.watchmovies.utils.JSONObjects;
import com.squareup.picasso.Picasso;

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
import static com.izuking.udacity.android.watchmovies.utils.NetworkUtils.IMGURL;

/**
 * Created by Izuking on 4/14/2017.
 */
public class DetailActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickListener {


    private RecyclerView movieTrailers;

    private TextView errMessage;
    private MovieAdapter movieAdapter;
    private Button addFavorite;
    private JSONObjects.MovieModel movieModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.title))) {
            movieModel = new JSONObjects().extractData(this, intent);
        }

        ImageView imageView = (ImageView) findViewById(R.id.backdrop);

        Picasso.with(this).load(IMGURL + movieModel.getPosterPath()).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_dialog_alert).into(imageView);


        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ((TextView) findViewById(R.id.movieTitle)).setText(movieModel.getOriginalTitle());
        ratingBar.setRating(Float.parseFloat(movieModel.getVoteAverage()));
        ((TextView) findViewById(R.id.releaseDate)).setText(movieModel.getReleaseDate());
        ((TextView) findViewById(R.id.synopsis)).setText(movieModel.getOverview());

        addFavorite = (Button) findViewById(R.id.addFavorite);

        if (isFavorite(movieModel.getTrailerId()) > 0)
            addFavorite.setText(getString(R.string.remove_favorite));

        TextView reviews;
        reviews = ((TextView) findViewById(R.id.reviews));

        for (int i = 0; i < movieModel.getReviewAuthors().size(); i++) {
            reviews.append("\tAUTHOR NAME: " + movieModel.getReviewAuthors().get(i) + "\n\n\tAUTHOR COMMENTS:\n" + movieModel.getReviewContents().get(i) + "\n\n");
        }

        movieTrailers = (RecyclerView) findViewById(R.id.rv_activity_main);
        errMessage = (TextView) findViewById(R.id.tv_error_message);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        movieTrailers.setLayoutManager(linearLayoutManager);
        movieTrailers.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this, this, MovieAdapter.UseCase.TRAILER_LIST);
        movieTrailers.setAdapter(movieAdapter);

        loadTrailers();
    }

    private void loadTrailers() {

        if (movieModel.getTrailerYoutubeKeys().size() > 0) {
            showMovieTrailersView();
            List<JSONObjects.MovieModel> movieModels = new ArrayList<>();
            movieModels.add(movieModel);
            movieAdapter.setMovieData(movieModels);
        } else {
            showErrorView();
        }
    }

    private void showErrorView() {
        movieTrailers.setVisibility(View.INVISIBLE);
        errMessage.setText(getString(R.string.no_trailer));
        errMessage.setVisibility(View.VISIBLE);
    }

    private void showMovieTrailersView() {
        errMessage.setVisibility(View.INVISIBLE);
        movieTrailers.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private int isFavorite(String trailerId) {
        int count = 0;
        try {
            Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, TRAILERID + "=?", new String[]{trailerId}, null);

            if (cursor != null)
                count = cursor.getCount();

        } catch (Exception e) {

            e.printStackTrace();

        }


        return count;
    }

    public void addFavorite(View view) {
        if (addFavorite.getText().equals(getString(R.string.remove_favorite))) {
            String selection = TRAILERID + "=?";
            String[] selectionArgs = new String[]{movieModel.getTrailerId()};

            int rows = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs);

            if (rows > 0) {
                Toast.makeText(this, movieModel.getOriginalTitle() + " Removed successfully.", Toast.LENGTH_LONG).show();
                addFavorite.setText(getString(R.string.add_favorite));
            } else
                Toast.makeText(this, "Unable to remove " + movieModel.getOriginalTitle() + "!", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(ORIGINALTITLE, movieModel.getOriginalTitle());
        values.put(POSTERPATH, movieModel.getPosterPath());
        values.put(OVERVIEW, movieModel.getOverview());
        values.put(VOTEAVERAGE, movieModel.getVoteAverage());
        values.put(RELEASEDATE, movieModel.getReleaseDate());
        values.put(REVIEWAUTHOR, MainActivity.convertListToString(movieModel.getReviewAuthors()));
        values.put(REVIEWCONTENT, MainActivity.convertListToString(movieModel.getReviewContents()));
        values.put(TRAILERID, movieModel.getTrailerId());

        values.put(MOVIE_TRAILERS_KEY, MainActivity.convertListToString(movieModel.getTrailerYoutubeKeys()));

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);


            Toast.makeText(this, movieModel.getOriginalTitle() + " added successfully." , Toast.LENGTH_LONG).show();
            addFavorite.setText(getString(R.string.remove_favorite));

    }


    @Override
    public void onclick(JSONObjects.MovieModel response, int position) {
        Intent contentIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + response.getTrailerYoutubeKeys().get(position)));
        Intent chooseBrowser = Intent.createChooser(contentIntent, "Choose browser of your choice");

        startActivity(chooseBrowser);
    }
}
