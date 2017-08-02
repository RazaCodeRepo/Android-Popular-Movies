package com.example.android.popularmovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kontrol on 7/28/2017.
 */

public class Movie {

    private String originalTitle;
    private String imageString;
    private String plotSynopsis;
    private String userRating;
    private String relaseDate;
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

