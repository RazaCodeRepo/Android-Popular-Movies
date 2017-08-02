package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailRecyclerViewAdapter.TrailerItemClickListener {

    public static final String TAG = "DetailActivity";

    private ListView trailerView;
    private ListView reviewView;

    private String movieID;
    private String movieTitle;
    private String movieImage;
    private String movieSummary;
    private String movieRating;
    private String movieDate;

    private static final int TMDB_TRAILER_LOADER = 243;
    private static final int TMDB_REVIEW_LOADER = 897;

    RecyclerView detailView;

    List<MovieReview> reviews;
    List<Trailer> trailers;

    DetailRecyclerViewAdapter detailRecyclerViewAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        detailView = (RecyclerView)findViewById(R.id.detail_activity_recycler);
        detailView.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();
        movieID = intent.getStringExtra("MOVIE_ID");
        movieTitle = intent.getStringExtra("MOVIE_TITLE");
        movieImage = intent.getStringExtra("MOVIE_IMAGE");
        movieSummary = intent.getStringExtra("MOVIE_SUMMARY");
        movieRating = intent.getStringExtra("MOVIE_RATING");
        movieDate = intent.getStringExtra("MOVIE_DATE");

        reviews = new ArrayList<MovieReview>();
        trailers = new ArrayList<Trailer>();

        Bundle trailerBundle = new Bundle();
        trailerBundle.putString("TMDB_TRAILER_URL",movieID);

        Bundle reviewBundle = new Bundle();
        reviewBundle.putString("TMDB_REVIEW_URL", movieID);;

        detailRecyclerViewAdapter = new DetailRecyclerViewAdapter(DetailActivity.this, movieTitle, movieImage, movieDate, movieRating, movieSummary, trailers, reviews, this);

        LoaderManager trailerLoaderManager = getSupportLoaderManager();
        Loader<List<Trailer>> trailerLoader = trailerLoaderManager.getLoader(TMDB_TRAILER_LOADER);
        if(trailerLoader == null){
            trailerLoaderManager.initLoader(TMDB_TRAILER_LOADER, trailerBundle, trailerLoaderCallbacks).forceLoad();
        }
        else{
            trailerLoaderManager.restartLoader(TMDB_TRAILER_LOADER, trailerBundle, trailerLoaderCallbacks).forceLoad();
        }

        LoaderManager reviewLoaderManager = getSupportLoaderManager();
        Loader<List<MovieReview>> reviewLoader = reviewLoaderManager.getLoader(TMDB_REVIEW_LOADER);
        if(reviewLoader == null){
            reviewLoaderManager.initLoader(TMDB_REVIEW_LOADER, reviewBundle, reviewLoaderCallbacks).forceLoad();
        }
        else{
            reviewLoaderManager.restartLoader(TMDB_REVIEW_LOADER, reviewBundle, reviewLoaderCallbacks).forceLoad();
        }



        detailView.setAdapter(detailRecyclerViewAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.favorite:
                if(item.getIcon().getConstantState().equals(getDrawable(R.drawable.star_off).getConstantState())){
                    item.setIcon(R.drawable.star_on);
                    addToDatabase(movieID, movieTitle, movieImage, movieDate, movieRating, movieSummary);
                }else{
                    Toast.makeText(this, "Un-Favorite", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.star_off);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private LoaderManager.LoaderCallbacks<List<Trailer>> trailerLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Trailer>>() {
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Trailer>>(DetailActivity.this) {

                public List<Trailer> loadInBackground() {
                    String trailerUrl = args.getString("TMDB_TRAILER_URL");
                    Log.v(TAG, trailerUrl);
                    if(trailerUrl == null || TextUtils.isEmpty(trailerUrl)){
                        return null;
                    }
                    List<Trailer> trailerList = NetworkUtils.extractTrailers(trailerUrl);

                    return trailerList;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
            detailRecyclerViewAdapter.setTrailers(data);
            trailers = data;
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<List<MovieReview>> reviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<MovieReview>>(DetailActivity.this) {
                @Override
                public List<MovieReview> loadInBackground() {
                    String trailerUrl = args.getString("TMDB_REVIEW_URL");
                    Log.v(TAG, trailerUrl);
                    if(trailerUrl == null || TextUtils.isEmpty(trailerUrl)){
                        return null;
                    }
                    List<MovieReview> movieList = NetworkUtils.extractReviews(trailerUrl);

                    return movieList;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
            detailRecyclerViewAdapter.setReviews(data);
        }

        @Override
        public void onLoaderReset(Loader<List<MovieReview>> loader) {

        }
    };

    @Override
    public void onTrailerItemClickListener(int clickedItemIndex) {
        int index = clickedItemIndex - 1;
        Trailer selectedTrailer = trailers.get(index);
        String trailerURL = selectedTrailer.getTrailerURL();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerURL));
        startActivity(intent);
    }

    private void addToDatabase(String id, String name, String image, String date, String rating, String summary){

        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE, image);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, name);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE, date);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, rating);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, summary);


        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if(uri != null){
            Toast.makeText(getBaseContext(), "Added To Favorites", Toast.LENGTH_SHORT).show();
        }
    }




}

