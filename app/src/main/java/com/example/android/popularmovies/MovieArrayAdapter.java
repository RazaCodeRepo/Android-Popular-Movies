package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Kontrol on 7/28/2017.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    public static final String LOG_TAG = MovieArrayAdapter.class.getName();

    private List<Movie> temp;

    Activity mContext;

    public MovieArrayAdapter(Activity context, List<Movie> moviesList){
        super(context, 0, moviesList);
        mContext = context;
        temp = moviesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.gird_item_view, parent, false);
        }

        Movie movie = getItem(position);

        ImageView posterImageView = (ImageView)listItemView.findViewById(R.id.main_iv_poster);
        String movieLocation = movie.getImageString();
        Log.v(LOG_TAG, movieLocation);
        Picasso.with(getContext()).setLoggingEnabled(true);
        Picasso.with(getContext()).load("https://image.tmdb.org/t/p/w500"+movieLocation)
                .placeholder(R.mipmap.ic_launcher).into(posterImageView);
        Log.v(LOG_TAG, "https://image.tmdb.org/t/p/w185"+movieLocation );

        return listItemView;
    }


}
