package com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
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

    private String movieID;
    private String movieTitle;
    private String movieImage;
    private String movieSummary;
    private String movieRating;
    private String movieDate;
    private int selectedMovieID;

    private static final int TMDB_TRAILER_LOADER = 243;
    private static final int TMDB_REVIEW_LOADER = 897;
    private static final int TMDB_DATABASE_LOADER = 956;

    private RecyclerView detailView;

    private List<MovieReview> reviews;
    private List<Trailer> trailers;

    private DetailRecyclerViewAdapter detailRecyclerViewAdapter;

    private boolean isInDatabase = false;



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

        getSupportLoaderManager().initLoader(TMDB_DATABASE_LOADER, null, cursorLoaderCallbacks).forceLoad();



        detailView.setAdapter(detailRecyclerViewAdapter);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (isInDatabase) {
            MenuItem menuItem = menu.findItem(R.id.action_favorite);
            menuItem.setIcon(R.drawable.star_on);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_favorite:
                if(item.getIcon().getConstantState().equals(getDrawable(R.drawable.star_off).getConstantState())){
                    item.setIcon(R.drawable.star_on);
                    addToDatabase(movieID, movieTitle, movieImage, movieDate, movieRating, movieSummary);
                }else{
                    String stringId = Integer.toString(selectedMovieID);
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();
                    getContentResolver().delete(uri,null,null);
                    Toast.makeText(this, getString(R.string.remove_favorites), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), getString(R.string.add_favorites), Toast.LENGTH_SHORT).show();
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>(){
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(DetailActivity.this) {

                Cursor mMovieData = null;

                @Override
                protected void onStartLoading() {
                    if (mMovieData != null){
                        deliverResult(mMovieData);
                    }else{
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    String[] projection = {MovieContract.MovieEntry._ID,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID };

                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, projection, null, null, null);
                }

                public void deliverResult(Cursor data){
                    mMovieData = data;
                    super.deliverResult(data);
                }
            };
        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            int _idColumnIndex = data.getColumnIndex(MovieContract.MovieEntry._ID);
            int movieIDColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            while(data.moveToNext()){
                int current_ID = data.getInt(_idColumnIndex);
                String currentMovieID = data.getString(movieIDColumnIndex);

                if(currentMovieID.equals(movieID)){
                    isInDatabase = true;
                    selectedMovieID = current_ID;
                    invalidateOptionsMenu();
                }
            }


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


}

