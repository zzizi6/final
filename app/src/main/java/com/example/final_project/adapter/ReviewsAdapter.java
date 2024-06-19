package com.example.final_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewsAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewWriter;
        private TextView textViewRating;
        private TextView textViewReview;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewMovieTitle);
            textViewWriter = itemView.findViewById(R.id.textViewWriter);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewReview = itemView.findViewById(R.id.textViewReview);
        }

        public void bind(Review review) {
            textViewTitle.setText(review.getTitle());
            textViewWriter.setText(review.getWriter());
            textViewRating.setText(String.valueOf(review.getRating()));
            textViewReview.setText(review.getReview());
        }
    }
}
