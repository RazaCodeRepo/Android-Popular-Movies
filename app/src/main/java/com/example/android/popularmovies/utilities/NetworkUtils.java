package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.MovieReview;
import com.example.android.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kontrol on 7/28/2017.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getName();
    //any changes

    private static final String TMDB_API_KEY = "";

    private static final String TMDB_TRAILER_REVIEWS = "https://api.themoviedb.org/3/movie/";

    public static List<Movie> extractMovies(String requestUrl) {

        Log.v(TAG, "Fetching Movie");

        List<Movie> movies = new ArrayList<Movie>();

        URL url = buildURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        movies = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}

        return movies;
    }

    public static URL buildURL(String sortOrder){
        Uri builtUri = Uri.parse(sortOrder).buildUpon()
                .appendQueryParameter("api_key", TMDB_API_KEY).build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch(MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built Uri " + url);

        return url;

    }

    private static String makeHttpRequest(URL url)throws IOException {
        Log.v(TAG, "In makeHttpREquest");
        String jsonResponse = "";

        if(url == null){
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the movie JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        //Log.v(LOG_TAG, jsonResponse);


        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.v(TAG, "In readFromStream");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Movie> extractFeatureFromJson(String moviesJSON) {
        Log.v(TAG, "In extractFeatureFromJSON");
        // If the JSON string is empty or null, then return early.
        ArrayList<Movie> movies = new ArrayList<>();
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        try {
            JSONObject root = new JSONObject(moviesJSON);
            JSONArray resultsArray = root.getJSONArray("results");

            for(int i=0;i<resultsArray.length();i++){

                JSONObject movieData = resultsArray.getJSONObject(i);
                String id = movieData.getString("id");
                String poster = movieData.getString("poster_path");
                String overview = movieData.getString("overview");
                String date = movieData.getString("release_date");
                String title = movieData.getString("original_title");
                String rating = movieData.getString("vote_average");

                movies.add(new Movie(id, title, poster, overview, rating, date));
            }
            return movies;
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the movies JSON results", e);
        }
        return null;
    }

    public static List<Trailer> extractTrailers(String movieID){
        Log.v(TAG, "In extractTrailers");

        String trailerEndpoint = TMDB_TRAILER_REVIEWS + movieID + "/videos";
        Log.v(TAG, "Trailer Endpoint:" + trailerEndpoint);

        List<Trailer> movieTrailers = new ArrayList<Trailer>();

        URL url = buildURL(trailerEndpoint);

        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(TAG, "Error closing input stream when getting trailers", e);
        }

        movieTrailers = extractFeatureFromTrailerJson(jsonResponse);
        return movieTrailers;
    }

    private static List<Trailer> extractFeatureFromTrailerJson(String trailerJson){
        Log.v(TAG, "In extractFeatureFromTrailerJSON");

        List<Trailer> trailersList = new ArrayList<Trailer>();

        if (TextUtils.isEmpty(trailerJson)) {
            return null;
        }

        try {
            JSONObject root = new JSONObject(trailerJson);
            JSONArray resultsArray = root.getJSONArray("results");
            for(int i=0; i<resultsArray.length(); i++){
                JSONObject trailerData = resultsArray.getJSONObject(i);
                String trailerID = trailerData.getString("id");
                String trailerKey = trailerData.getString("key");
                String trailerName = trailerData.getString("name");
                String trailerSite = trailerData.getString("site");
                Log.v(TAG, trailerKey + trailerName + trailerSite);
                trailersList.add(new Trailer(trailerID, trailerKey, trailerName, trailerSite));
            }
            return trailersList;

        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the trailer JSON results", e);
        }

        return null;
    }

    public static List<MovieReview> extractReviews(String movieID){
        Log.v(TAG, "In extractTrailers");
        Log.v(TAG, "Trailer Endpoint:" + movieID);

        String reviewEndpoint = TMDB_TRAILER_REVIEWS + movieID + "/reviews";

        ArrayList<MovieReview> movieReviews = new ArrayList<MovieReview>();

        URL url = buildURL(reviewEndpoint);

        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(TAG, "Error closing input stream when getting trailers", e);
        }

        movieReviews = extractFeatureFromReviewJson(jsonResponse);
        return movieReviews;
    }

    private static ArrayList<MovieReview> extractFeatureFromReviewJson(String reviewJson){
        Log.v(TAG, "In extractFeatureFromTrailerJSON");

        ArrayList<MovieReview> reviewList = new ArrayList<MovieReview>();

        if (TextUtils.isEmpty(reviewJson)) {
            return null;
        }

        try {
            JSONObject root = new JSONObject(reviewJson);
            JSONArray resultsArray = root.getJSONArray("results");
            for(int i=0; i<resultsArray.length(); i++){
                JSONObject trailerData = resultsArray.getJSONObject(i);
                String reviewID = trailerData.getString("id");
                String reviewAuthor = trailerData.getString("author");
                String reviewURL = trailerData.getString("url");
                String reviewContent = trailerData.getString("content");
                Log.v(TAG, reviewID + reviewAuthor + reviewURL);
                reviewList.add(new MovieReview(reviewID, reviewAuthor, reviewURL, reviewContent));
            }
            return reviewList;

        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the trailer JSON results", e);
        }

        return null;
    }
}

