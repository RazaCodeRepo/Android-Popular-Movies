package com.example.android.popularmovies;

import android.content.Context;


import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getName();

    private static final String TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?";

    private static final String TMDB_RATING_URL = "https://api.themoviedb.org/3/movie/top_rated?";

    private TextView mErrorMessageDisplay;

    private ProgressBar progressBar;

    private GridView moviesGrid;

    private MovieArrayAdapter mMovieAdapter;

    private static final int TMDB_QUERY_LOADER = 122;

    private static final int TMDB_CURSOR_LOADER = 345;

    private LoaderManager loaderManager;

    private static final String LOADER_TMDB_BUNDLE = "stringURL";

    private MovieCursorAdapter movieCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG,"IN the main acitivity");

        setContentView(R.layout.activity_main);

        moviesGrid = (GridView)findViewById(R.id.movie_grid);

        progressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);

        movieCursorAdapter = new MovieCursorAdapter(this, null);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("MOVIE_ID", movie.getMovieID());
                intent.putExtra("MOVIE_TITLE", movie.getOriginalTitle());
                intent.putExtra("MOVIE_IMAGE", movie.getImageString());
                intent.putExtra("MOVIE_SUMMARY", movie.getPlotSynopsis());
                intent.putExtra("MOVIE_RATING", movie.getUserRating());
                intent.putExtra("MOVIE_DATE", movie.getRelaseDate());


                startActivity(intent);
            }
        });

        Bundle queryBundle = new Bundle();
        queryBundle.putString(LOADER_TMDB_BUNDLE, TMDB_POPULAR_URL);


        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            loaderManager = getSupportLoaderManager();
            Loader<List<Movie>> movieLoader = loaderManager.getLoader(TMDB_QUERY_LOADER);
            if(movieLoader == null){
                loaderManager.initLoader(TMDB_QUERY_LOADER, queryBundle, movieLoaderCallbacks).forceLoad();
            }
            else{
                loaderManager.restartLoader(TMDB_QUERY_LOADER, queryBundle, movieLoaderCallbacks);
            }
        }
        else{
            progressBar.setVisibility(View.GONE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_popular:
                Bundle popularityBundle = new Bundle();
                popularityBundle.putString(LOADER_TMDB_BUNDLE, TMDB_POPULAR_URL);
                Toast.makeText(this, getResources().getString(R.string.popularity_sort), Toast.LENGTH_SHORT).show();
                loaderManager.restartLoader(TMDB_QUERY_LOADER, popularityBundle, movieLoaderCallbacks).forceLoad();
                return true;

            case R.id.menu_rating:
                Bundle ratingsBundle = new Bundle();
                ratingsBundle.putString(LOADER_TMDB_BUNDLE, TMDB_RATING_URL);
                Toast.makeText(this, getResources().getString(R.string.ratings_sort), Toast.LENGTH_SHORT).show();
                loaderManager.restartLoader(TMDB_QUERY_LOADER, ratingsBundle, movieLoaderCallbacks).forceLoad();
                setTitle("Sorted By Ratings");
                return true;

            case R.id.menu_favorite:
                Toast.makeText(this, getString(R.string.favorite_sort), Toast.LENGTH_SHORT).show();
                movieCursorAdapter = new MovieCursorAdapter(this, null);
                moviesGrid.setAdapter(movieCursorAdapter);
                getSupportLoaderManager().initLoader(TMDB_CURSOR_LOADER, null, cursorLoaderCallbacks).forceLoad();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showMovieData() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        moviesGrid.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        moviesGrid.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }



    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>(){
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(MainActivity.this) {

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
                            MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE,
                            MovieContract.MovieEntry.COLUMN_MOVIE_NAME };

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
            movieCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            movieCursorAdapter.swapCursor(null);
        }
    };

    private LoaderManager.LoaderCallbacks<List<Movie>> movieLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Movie>>(){
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<Movie>>(MainActivity.this){


                @Override
                protected void onStartLoading() {
                    if(args == null){
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public List<Movie> loadInBackground() {
                    String tmdbSearchQuery = args.getString(LOADER_TMDB_BUNDLE);
                    Log.v(TAG, tmdbSearchQuery);
                    if(tmdbSearchQuery == null || TextUtils.isEmpty(tmdbSearchQuery)){
                        return null;
                    }
                    List<Movie> moviesList = NetworkUtils.extractMovies(tmdbSearchQuery);
                    return moviesList;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
            progressBar.setVisibility(View.INVISIBLE);
            if(data != null){
                showMovieData();
                mMovieAdapter = new MovieArrayAdapter(MainActivity.this, data);
                moviesGrid.setAdapter(mMovieAdapter);
            }
            else{
                showErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {

        }
    };

}

