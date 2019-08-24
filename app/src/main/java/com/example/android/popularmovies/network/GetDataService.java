package com.example.android.popularmovies.network;

import com.example.android.popularmovies.model.MoviesList;
import com.example.android.popularmovies.model.ReviewList;
import com.example.android.popularmovies.model.TrailersList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("3/movie/{sorting}")
    Call<MoviesList> getAllMovies(@Path("sorting") String sortingKey, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<TrailersList> getAllTrailers(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewList> getAllReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

}
