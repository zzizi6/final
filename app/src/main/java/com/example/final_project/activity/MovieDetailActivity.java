package com.example.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.model.Movie;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MovieDetailActivity extends AppCompatActivity {

    // 영화 세부사항 변수
    private TextView textViewMovieTitle;
    private TextView textViewGenre;
    private TextView textViewDirector;
    private TextView textViewReleaseDate;
    private TextView textViewPlot;
    private ImageView imageViewPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // 인텐트로 전달된 드라마 정보 받기
        Movie movie = getIntent().getParcelableExtra("movie");

        // 화면 요소 초기화
        textViewMovieTitle = findViewById(R.id.textViewMovieTitle);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewDirector = findViewById(R.id.textViewDirector);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewPlot = findViewById(R.id.textViewPlot);
        imageViewPoster = findViewById(R.id.imageViewPoster);

        // 드라마 정보 설정
        if (movie != null) {
            textViewMovieTitle.setText(movie.getTitle());
            textViewGenre.setText(movie.getGenre());
            textViewDirector.setText(movie.getDirector());
            textViewReleaseDate.setText(movie.getReleaseDate());
            textViewPlot.setText(movie.getPlot());

            // Glide를 사용하여 포스터 이미지 설정
            Glide.with(this)
                    .load(movie.getPosterUrl())
                    //.placeholder(R.drawable.placeholder_image) // 로딩 중에 보여질 이미지
                    //.error(R.drawable.error_image) // 이미지 로드 실패 시 보여질 이미지
                    .into(imageViewPoster);
        }

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // 메뉴 아이템의 id 가져오기

            if (itemId == R.id.page_home) {
                // 홈 화면으로 이동
                startActivity(MainActivity.class);
                return true;
            } else if (itemId == R.id.page_movie_list) {
                // 영화 리스트 화면으로 이동
                startActivity(MovieListActivity.class);
                return true;
            } else if (itemId == R.id.page_movie_review) {
                // 리뷰 화면으로 이동
                startActivity(ReviewsActivity.class);
                return true;
            } else if (itemId == R.id.page_user_rating) {
                // 평점/리뷰 화면으로 이동
                startActivity(UserRatingActivity.class);
                return true;
            } else if (itemId == R.id.page_info) {
                // 정보 화면으로 이동
                startActivity(InfoActivity.class);
                return true;
            }

            return false;
        });
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(MovieDetailActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
