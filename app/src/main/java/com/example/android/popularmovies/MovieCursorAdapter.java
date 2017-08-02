package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Kontrol on 7/29/2017.
 */

public class MovieCursorAdapter extends CursorAdapter {

    public MovieCursorAdapter(Context context, Cursor c) {

        super(context, c, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){

        return LayoutInflater.from(context).inflate(R.layout.gird_item_view, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor){

        ImageView imageView = (ImageView)view.findViewById(R.id.main_iv_poster);

        String imageURL = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE));
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context).load("https://image.tmdb.org/t/p/w500"+ imageURL)
                .placeholder(R.mipmap.ic_launcher).into(imageView);

    }


}
