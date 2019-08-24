package com.example.android.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.android.popularmovies.adapters.DetailRecyclerViewAdapter;
import com.example.android.popularmovies.model.MovieReview;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.ReviewList;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.model.TrailersList;
import com.example.android.popularmovies.network.GetDataService;
import com.example.android.popularmovies.network.RetrofitClientInstance;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements DetailRecyclerViewAdapter.TrailerItemClickListener {

    public static final String TAG = "DetailActivity";

    private String movieID;
    private String movieTitle;
    private String movieImage;
    private String movieSummary;
    private String movieRating;
    private String movieDate;
    private int selectedMovieID;
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

        detailRecyclerViewAdapter = new DetailRecyclerViewAdapter(DetailActivity.this, movieTitle, movieImage, movieDate, movieRating, movieSummary, trailers, reviews, this);

        getSupportLoaderManager().initLoader(TMDB_DATABASE_LOADER, null, cursorLoaderCallbacks).forceLoad();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<TrailersList> call = service.getAllTrailers(Integer.parseInt(movieID), "e5e3fe86c705926ad4e294aea744d322");
        Log.v(TAG, call.request().url().toString());
        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {
                trailers = response.body().getResults();
                detailRecyclerViewAdapter.setTrailers(trailers);
            }

            @Override
            public void onFailure(Call<TrailersList> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

        Call<ReviewList> reviewListCall = service.getAllReviews(Integer.parseInt(movieID), "e5e3fe86c705926ad4e294aea744d322");
        reviewListCall.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                List<MovieReview> reviews = response.body().getResults();
                detailRecyclerViewAdapter.setReviews(reviews);
            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {

            }
        });
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

