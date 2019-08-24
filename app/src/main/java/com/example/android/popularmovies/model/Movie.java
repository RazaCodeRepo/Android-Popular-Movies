package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kontrol on 7/28/2017.
 */

public class Movie {

    @SerializedName("title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String imageString;

    @SerializedName("overview")
    private String plotSynopsis;

    @SerializedName("vote_average")
    private String userRating;

    @SerializedName("release_date")
    private String relaseDate;

    @SerializedName("id")
    private String movieID;

    public Movie(String id, String title, String image, String synopsis, String rating, String date){
        movieID = id;
        originalTitle = title;
        imageString = image;
        plotSynopsis = synopsis;
        userRating = rating;
        relaseDate = date;
    }

    public String getMovieID(){return movieID; }

    public String getOriginalTitle(){
        return originalTitle;
    }

    public String getImageString() {
        return imageString;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public String getRelaseDate() {
        return relaseDate;
    }

    public String getUserRating() {
        return userRating;
    }

    public String toString(){
        return movieID + " " + originalTitle + " " + imageString + " " + plotSynopsis + " " + userRating + " " + relaseDate;
    }
}


