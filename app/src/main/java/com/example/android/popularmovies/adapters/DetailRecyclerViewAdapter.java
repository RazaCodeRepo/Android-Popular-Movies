package com.example.android.popularmovies.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.MovieReview;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Kontrol on 8/1/2017.
 */

public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final TrailerItemClickListener trailerItemClickListener;



    public interface TrailerItemClickListener{
        void onTrailerItemClickListener(int clickedItemIndex);
    }



    private Context mContext;

    private String name;
    private String image;
    private String date;
    private String ratings;
    private String summary;

    private List<Trailer> trailers;
    private List<MovieReview> reviews;

    private final int MOVIE = 0;
    private final int REVIEW = 2;
    private final int TRAILER = 1;

    public DetailRecyclerViewAdapter(Context context, String nam, String img, String dat, String rat, String sum, List<Trailer> trail, List<MovieReview> rev, TrailerItemClickListener trailerItemClick){
        mContext = context;
        name = nam;
        image = img;
        date = dat;
        ratings = rat;
        summary = sum;
        trailers = trail;
        reviews = rev;
        trailerItemClickListener = trailerItemClick;

    }

    public int getItemViewType(int position){
        if (position == 0){
            return MOVIE;
        }

        if(position > 0 && position <= trailers.size()){
            return TRAILER;
        } else{
            return REVIEW;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == MOVIE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_main_item_view, parent, false);
            MainViewHolder mainViewHolder = new MainViewHolder(view);
            return mainViewHolder;
        } else if(viewType == TRAILER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_trailer_item_view, parent, false);
            TrailerViewHolder trailerViewHolder = new TrailerViewHolder(view);
            return trailerViewHolder;
        } else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_review_item_view, parent, false);
            ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);
            return reviewViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder.getItemViewType() == MOVIE){
            MainViewHolder mainViewHolder = (MainViewHolder)holder;

            mainViewHolder.nameView.setText(name);

            Picasso.with(mContext).setLoggingEnabled(true);
            Picasso.with(mContext).load("https://image.tmdb.org/t/p/w500"+image).placeholder(R.mipmap.ic_launcher).into(mainViewHolder.posterView);

            mainViewHolder.dateView.setText(date);
            mainViewHolder.ratingsView.setText(ratings +"/10");
            mainViewHolder.summaryView.setText(summary);



        } else if(holder.getItemViewType() == TRAILER){
            TrailerViewHolder trailerViewHolder = (TrailerViewHolder)holder;

            trailerViewHolder.trailerTextView.setText("Trailer " + String.valueOf(position));
            Picasso.with(mContext).setLoggingEnabled(true);
            Picasso.with(mContext).load(trailers.get(position -1).getTrailer_thumbnail()).placeholder(R.mipmap.ic_launcher).into(trailerViewHolder.trailerThumbnailView);

        } else{
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder)holder;

            reviewViewHolder.reviewAuthorView.setText(mContext.getString(R.string.tv_reviewTag) + " " + reviews.get(position-1 - trailers.size()).getReviewAuthor());
            reviewViewHolder.reviewLinkView.setText(reviews.get(position-1 - trailers.size()).getReviewContent());
        }
    }

    @Override
    public int getItemCount() {
        return 1 + trailers.size() + reviews.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        ImageView posterView;
        TextView dateView;
        TextView ratingsView;
        TextView summaryView;


        public MainViewHolder(View itemView){
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.detail_tv_movieTitle);
            posterView = (ImageView)itemView.findViewById(R.id.detail_iv_poster);
            dateView = (TextView)itemView.findViewById(R.id.detail_tv_date);
            ratingsView = (TextView)itemView.findViewById(R.id.detail_tv_ratings);
            summaryView = (TextView)itemView.findViewById(R.id.detail_tv_synopsis);

        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView reviewAuthorView;
        TextView reviewLinkView;

        public ReviewViewHolder(View itemView){
            super(itemView);
            reviewAuthorView = (TextView)itemView.findViewById(R.id.review_author);
            reviewLinkView = (TextView)itemView.findViewById(R.id.review_link);

        }


    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView trailerThumbnailView;
        TextView trailerTextView;

        public TrailerViewHolder(View itemView){
            super(itemView);

            trailerThumbnailView = (ImageView)itemView.findViewById(R.id.trailer_image);
            trailerTextView = (TextView)itemView.findViewById(R.id.trailer_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            trailerItemClickListener.onTrailerItemClickListener(clickedPosition);
        }
    }

    public void setTrailers(List<Trailer> trailer){
        trailers = trailer;
        notifyDataSetChanged();
    }

    public void setReviews(List<MovieReview>  review){
        reviews = review;
        notifyDataSetChanged();
    }


}
