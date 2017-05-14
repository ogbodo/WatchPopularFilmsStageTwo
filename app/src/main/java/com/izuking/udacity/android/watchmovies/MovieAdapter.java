package com.izuking.udacity.android.watchmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.izuking.udacity.android.watchmovies.utils.JSONObjects;
import com.izuking.udacity.android.watchmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Izuking on 4/15/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {


    private UseCase useCase;
    private MovieAdapterOnClickListener mClick;
    private Context cxt;
    private List<JSONObjects.MovieModel> movieList;


    public MovieAdapter(Context cxt, MovieAdapterOnClickListener click, UseCase useCase) {
        this.cxt = cxt;
        this.mClick = click;
        this.useCase = useCase;
    }

    public void setMovieData(List<JSONObjects.MovieModel> movieList) {
        this.movieList = movieList;

        notifyDataSetChanged();
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;

        if (useCase.equals(UseCase.FROM_API) || useCase.equals(UseCase.FROM_DATABASE)) {
            view = inflater.inflate(R.layout.movie_list, parent, false);

        } else if (useCase.equals(UseCase.TRAILER_LIST)) {
            view = inflater.inflate(R.layout.movie_trailer_list, parent, false);
        }

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        if (holder == null)
            return;
        if (useCase.equals(UseCase.FROM_API) || useCase.equals(UseCase.FROM_DATABASE)) {
            Picasso.with(cxt).load(NetworkUtils.IMGURL + movieList.get(position).getPosterPath()).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_dialog_alert).into(holder.posterImage);

        } else if (useCase.equals(UseCase.TRAILER_LIST)) {
            holder.listItem.setText("Trailer #" + Integer.toString(position + 1));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (movieList == null)
            return count;


        if (useCase.equals(UseCase.FROM_API) || useCase.equals(UseCase.FROM_DATABASE))
            count = movieList.size();

        else if (useCase.equals(UseCase.TRAILER_LIST))
            count = movieList.get(count).getTrailerYoutubeKeys().size();


        return count;
    }

    enum UseCase {FROM_API, FROM_DATABASE, TRAILER_LIST}

    //Interface to be implemented by MainActivity
    public interface MovieAdapterOnClickListener {

        void onclick(JSONObjects.MovieModel response, int position);
    }


    //The view adapter implementation class
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView posterImage;
        protected TextView listItem;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            if (itemView == null)
                return;

            if (useCase.equals(UseCase.FROM_API) || useCase.equals(UseCase.FROM_DATABASE)) {
                posterImage = (ImageView) itemView.findViewById(R.id.movie_poster);


            } else if (useCase.equals(UseCase.TRAILER_LIST)) {
                listItem = (TextView) itemView.findViewById(R.id.trailersItem);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (useCase.equals(UseCase.FROM_API) || useCase.equals(UseCase.FROM_DATABASE)) {

                mClick.onclick(movieList.get(getAdapterPosition()), getAdapterPosition());

            } else if (useCase.equals(UseCase.TRAILER_LIST)) {
                mClick.onclick(movieList.get(0), getAdapterPosition());

            }

        }
    }
}
