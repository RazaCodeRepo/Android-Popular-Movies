package com.example.android.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kontrol on 7/28/2017.
 */

public class Trailer {

    private final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private final String YOUTUBE_APP_INTENT = "https://img.youtube.com/vi/";

    @SerializedName("id")
    private String trailerID;

    @SerializedName("key")
    private String trailerKey;

    @SerializedName("name")
    private String trailerName;

    @SerializedName("site")
    private String trailerSite;

    private String trailer_thumbnail;

    private static List<Trailer> temp = new ArrayList<Trailer>();

    public Trailer(String id, String key, String name, String site){
        trailerID = id;
        trailerKey = key;
        trailerName = name;
        trailerSite = site;

    }

    public String getTrailerID(){return trailerID; };

    public String getTrailerKey(){
        return trailerKey;
    }

    public String getTrailerSite(){
        return trailerSite;
    }

    public String getTrailerName(){
        return trailerName;
    }

    public String getTrailerURL(){
        return YOUTUBE_URL + trailerKey;
    }

    public String getTrailer_thumbnail(){
        return YOUTUBE_APP_INTENT + trailerKey + "/hqdefault.jpg";
    }

    public String toString(){
        return YOUTUBE_URL + trailerKey;
    }


}

