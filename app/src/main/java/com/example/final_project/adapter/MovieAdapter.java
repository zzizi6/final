package com.example.final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.db.DatabaseHelper;
import com.example.final_project.model.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private OnItemClickListener listener;
    private DatabaseHelper databaseHelper;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MovieAdapter(List<Movie> movieList, OnItemClickListener listener, Context context) {
        this.movieList = movieList;
        this.listener = listener;
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewGenre.setText(movie.getGenre());
        holder.textViewReleaseDate.setText(movie.getReleaseDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));

//        if (databaseHelper.isFavoriteMovie(movie.getTitle())) {
//            holder.buttonFavorite.setImageResource(R.drawable.ic_heart);
//        } else {
//            holder.buttonFavorite.setImageResource(R.drawable.ic_heart);
//        }

        holder.buttonFavorite.setOnClickListener(v -> onHeartButtonClick(position));
    }

    private void onHeartButtonClick(int position) {
        Movie movie = movieList.get(position);
        if (databaseHelper.isFavoriteMovie(movie.getTitle())) {
            databaseHelper.removeFavoriteMovie(movie.getTitle());
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.addFavoriteMovie(movie.getTitle());
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewGenre, textViewReleaseDate;
        ImageButton buttonFavorite;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewMovieTitle);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            textViewReleaseDate = itemView.findViewById(R.id.textViewReleaseDate);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }
    }
}
