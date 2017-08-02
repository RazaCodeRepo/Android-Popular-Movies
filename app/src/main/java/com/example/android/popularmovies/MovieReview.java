package com.example.android.popularmovies;

/**
 * Created by Kontrol on 7/31/2017.
 */

public class MovieReview {

    private String reviewID;

    private String reviewAuthor;

    private String reviewURL;

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
