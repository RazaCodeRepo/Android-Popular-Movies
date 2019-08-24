package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kontrol on 7/31/2017.
 */

public class MovieReview {

    @SerializedName("id")
    private String reviewID;

    @SerializedName("author")
    private String reviewAuthor;

    @SerializedName("url")
    private String reviewURL;

    @SerializedName("content")
    private String reviewContent;

    public MovieReview(String id, String author, String url, String content){
        reviewID = id;
        reviewAuthor = author;
        reviewURL = url;
        reviewContent = content;
    }

    public String getReviewAuthor(){
        return reviewAuthor;
    }

    public String getReviewURL(){
        return reviewURL;
    }

    public String getReviewContent(){return reviewContent;}
}
