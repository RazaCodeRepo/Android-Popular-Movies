package com.example.android.popularmovies.activities;

import android.content.Context;


import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.adapters.MovieArrayAdapter;
import com.example.android.popularmovies.adapters.MovieCursorAdapter;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.MoviesList;
import com.example.android.popularmovies.network.GetDataService;
import com.example.android.popularmovies.network.RetrofitClientInstance;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getName();

    private TextView mErrorMessageDisplay;

    private ProgressBar progressBar;

    private GridView moviesGrid;

    private MovieArrayAdapter mMovieAdapter;

    private static final int TMDB_CURSOR_LOADER = 345;

    private static final String LOADER_TMDB_BUNDLE = "stringURL";

    private MovieCursorAdapter movieCursorAdapter;


    private int selectedMovieID;

    boolean checkPopularity = false;
    boolean checkRatings = false;
    boolean checkFavorites = false;

    private int scrollPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG,"IN the main acitivity");

        setContentView(R.layout.activity_main);

        moviesGrid = (GridView)findViewById(R.id.movie_grid);

        progressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);

        movieCursorAdapter = new MovieCursorAdapter(this, null);

        if(savedInstanceState != null){
            if(savedInstanceState.getBoolean("POPULARITY_CHECK")){
                scrollPosition = savedInstanceState.getInt("SCROLL_POSITION");

                setMoviesList("popular");

                checkPopularity = true;
                checkRatings = false;
                checkFavorites = false;
            } else if(savedInstanceState.getBoolean("RATING_CHECK")){
                scrollPosition = savedInstanceState.getInt("SCORLL_POSITION");

                setMoviesList("top_rated");

                checkRatings = true;
                checkPopularity = false;
                checkFavorites = false;
            } else if (savedInstanceState.getBoolean("FAVORITE_CHECK")){
                scrollPosition = savedInstanceState.getInt("SCORLL_POSITION");
                getSupportLoaderManager().initLoader(TMDB_CURSOR_LOADER, null, cursorLoaderCallbacks).forceLoad();
                checkFavorites = true;
                checkPopularity = false;
                checkRatings = false;
            }
        } else {
<<<<<<< HEAD:app/src/main/java/com/example/android/popularmovies/activities/MainActivity.java
            progressBar.setVisibility(View.VISIBLE);
            setMoviesList("popular");
            checkPopularity = true;
=======


            Bundle queryBundle = new Bundle();

            queryBundle.putString(LOADER_TMDB_BUNDLE, TMDB_POPULAR_URL);

            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                loaderManager = getSupportLoaderManager();
                Loader<List<Movie>> movieLoader = loaderManager.getLoader(TMDB_QUERY_LOADER);
                if (movieLoader == null) {

                    loaderManager.initLoader(TMDB_QUERY_LOADER, queryBundle, movieLoaderCallbacks).forceLoad();
                    checkPopularity = true;
                    checkRatings = false;
                    checkFavorites = false;
                    Log.v(TAG, "In init movie loader");
                } else {
                    loaderManager.restartLoader(TMDB_QUERY_LOADER, queryBundle, movieLoaderCallbacks).forceLoad();
                    checkPopularity = true;
                    checkRatings = false;
                    checkFavorites = false;
                    Log.v(TAG, "In restart movie loader");
                }
            } else {
                progressBar.setVisibility(View.GONE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
>>>>>>> 1a98da1b7301c5ed260e7e0bc480e3103ca7d845:app/src/main/java/com/example/android/popularmovies/MainActivity.java
        }

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                intent.putExtra("SELECTED_ID", Integer.toString(selectedMovieID));


                startActivity(intent);
            }
        });

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
                setMoviesList("popular");

                checkPopularity = true;
                checkRatings = false;
                checkFavorites = false;

                Toast.makeText(this, getResources().getString(R.string.popularity_sort), Toast.LENGTH_SHORT).show();

                return true;

            case R.id.menu_rating:
                setMoviesList("top_rated");

                checkRatings = true;
                checkPopularity = false;
                checkFavorites = false;
                setTitle(R.string.ratings_sort);

                Toast.makeText(this, getResources().getString(R.string.ratings_sort), Toast.LENGTH_SHORT).show();

                return true;

            case R.id.menu_favorite:

                movieCursorAdapter = new MovieCursorAdapter(this, null);
                moviesGrid.setAdapter(movieCursorAdapter);
                getSupportLoaderManager().initLoader(TMDB_CURSOR_LOADER, null, cursorLoaderCallbacks).forceLoad();
                Toast.makeText(this, getString(R.string.favorite_sort), Toast.LENGTH_SHORT).show();
                checkFavorites = true;
                checkPopularity = false;
                checkRatings = false;
                setTitle(R.string.favorite_sort);
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
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE,
                            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
                            MovieContract.MovieEntry.COLUMN_MOVIE_DATE,
                            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
                            MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS };

                    Log.v(TAG, "In loadInBackground cursor loader");

                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, projection, null, null, null);
                }

                public void deliverResult(Cursor data){
                    super.deliverResult(data);
                    mMovieData = data;
                    Log.v(TAG, "In deliverResult cursor loader");
                }
            };
        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            movieCursorAdapter.swapCursor(data);
            List<Movie> dbMovies = new ArrayList<Movie>();
            int _idColumnIndex = data.getColumnIndex(MovieContract.MovieEntry._ID);
            int movieIDColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int movieTitleColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
            int movieImageColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE);
            int movieDateColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_DATE);
            int movieRatingColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING);
            int movieSynopsisColumnIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS);
            while(data.moveToNext()){
                int current_ID = data.getInt(_idColumnIndex);
                String currentMovieID = data.getString(movieIDColumnIndex);
                String currentMovieTitle = data.getString(movieTitleColumnIndex);
                String currentMovieImage = data.getString(movieImageColumnIndex);
                String currentMovieDate = data.getString(movieDateColumnIndex);
                String currentMovieRatings = data.getString(movieRatingColumnIndex);
                String currentMovieSynopsis = data.getString(movieSynopsisColumnIndex);
                selectedMovieID = current_ID;
                Log.v(TAG, Integer.toString(selectedMovieID));
                dbMovies.add(new Movie(currentMovieID, currentMovieTitle, currentMovieImage, currentMovieSynopsis, currentMovieRatings, currentMovieDate ));
            }
            mMovieAdapter = new MovieArrayAdapter(MainActivity.this, dbMovies);
            moviesGrid.setAdapter(mMovieAdapter);
            moviesGrid.smoothScrollToPosition(scrollPosition);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            movieCursorAdapter.swapCursor(null);
        }
    };

    private void setMoviesList(String sortBy){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call<MoviesList> call = service.getAllMovies(sortBy, "e5e3fe86c705926ad4e294aea744d322");
            Log.v(TAG, call.request().url().toString());
            call.enqueue(new Callback<MoviesList>() {
                @Override
                public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                    progressBar.setVisibility(View.GONE);
                    List<Movie> movies = response.body().getResults();
                    mMovieAdapter = new MovieArrayAdapter(MainActivity.this, movies);
                    moviesGrid.setAdapter(mMovieAdapter);
                    moviesGrid.smoothScrollToPosition(scrollPosition);
                }

                @Override
                public void onFailure(Call<MoviesList> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            progressBar.setVisibility(View.GONE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("POPULARITY_CHECK", checkPopularity);
        outState.putBoolean("RATING_CHECK", checkRatings);
        outState.putBoolean("FAVORITE_CHECK", checkFavorites);
        scrollPosition = moviesGrid.getFirstVisiblePosition();
        outState.putInt("SCROLL_POSITION", scrollPosition);
    }
}

