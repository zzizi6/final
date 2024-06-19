package com.example.final_project.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.model.Movie;
import java.util.List;

public class MovieDetailAdapter extends RecyclerView.Adapter<MovieDetailAdapter.DramaViewHolder> {

    private List<Movie> movieList; // 드라마 목록을 담는 리스트
    private OnItemClickListener listener; // 아이템 클릭 리스너 인터페이스


    // 아이템 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // 생성자: 드라마 목록과 클릭 리스너를 초기화
    public MovieDetailAdapter(List<Movie> movieList, OnItemClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    // onCreateViewHolder: 아이템 뷰를 위한 ViewHolder 객체 생성
    @NonNull
    @Override
    public DramaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃 파일을 인플레이트하여 ViewHolder 객체 생성
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new DramaViewHolder(itemView);
    }

    // onBindViewHolder: ViewHolder에 데이터를 바인딩
    @Override
    public void onBindViewHolder(@NonNull DramaViewHolder holder, int position) {
        Movie movie = movieList.get(position); // 해당 위치(position)의 드라마 객체 가져오기
        holder.bind(movie, listener); // ViewHolder에 데이터를 바인딩
    }

    // getItemCount: 데이터셋의 크기 반환 (드라마 목록의 크기)
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // DramaViewHolder: RecyclerView의 각 아이템을 위한 ViewHolder 클래스
    public static class DramaViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle; // 드라마 제목을 표시하는 TextView
        private TextView textViewGenre; // 드라마 장르를 표시하는 TextView
        private TextView textViewDirector; // 드라마 감독을 표시하는 TextView
        private TextView textViewReleaseDate; // 드라마 개봉일을 표시하는 TextView
        private TextView textViewPlot; // 드라마 줄거리를 표시하는 TextView
        private ImageView imageViewPoster; // 드라마 포스터를 표시하는 ImageView

        // ViewHolder 생성자: 아이템 뷰의 각 요소를 findViewById로 초기화
        public DramaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewMovieTitle); // 제목 TextView 초기화
            textViewGenre = itemView.findViewById(R.id.textViewGenre); // 장르 TextView 초기화
            textViewDirector = itemView.findViewById(R.id.textViewDirector); // 감독 TextView 초기화
            textViewReleaseDate = itemView.findViewById(R.id.textViewReleaseDate); // 개봉일 TextView 초기화
            textViewPlot = itemView.findViewById(R.id.textViewPlot); // 줄거리 TextView 초기화
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster); // 포스터 ImageView 초기화
        }

        // bind 메서드: 드라마 객체와 클릭 리스너를 ViewHolder에 바인딩
        @SuppressLint("SetTextI18n")
        public void bind(final Movie movie, final OnItemClickListener listener) {

            textViewTitle.setText(movie.getTitle()); // 드라마 제목 설정
            textViewGenre.setText(movie.getGenre()); // 드라마 장르 설정
            textViewDirector.setText(movie.getDirector()); // 드라마 감독 설정
            textViewReleaseDate.setText(movie.getReleaseDate()); // 드라마 개봉일 설정
            textViewPlot.setText(movie.getPlot()); // 드라마 줄거리 설정

            // Glide를 사용하여 포스터 이미지를 설정
            Glide.with(itemView.getContext())
                    .load(movie.getPosterUrl())
                    .into(imageViewPoster);

            // 아이템 뷰 클릭 시
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition()); // 클릭 리스너의 onItemClick 호출
                }
            });
        }
    }

}
