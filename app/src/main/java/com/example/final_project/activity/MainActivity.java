package com.example.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.api.ApiExplorer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String apiKey = "W4TU366107F7CKDTG148";
    private static final int listCount = 30;
    private static final int startCount = 1;
    private static String current_user;
    private static List<ApiExplorer.SimpleMovie> movieList;
    private boolean apiCalled = false;

    // UI 요소들
    private TextView titleTextView;
    private TextView genreTextView;
    private TextView directorTextView;
    private TextView plotTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 초기화
        titleTextView = findViewById(R.id.textViewMainTitle);
        genreTextView = findViewById(R.id.textViewMainGenre);
        directorTextView = findViewById(R.id.textViewMainDirector);
        plotTextView = findViewById(R.id.textViewMainStory);

        // 현재 유저 아이디 받아옴
        current_user = getIntent().getStringExtra("current_user");
        assert current_user != null;
        Log.d("current_user -> ", current_user);

        // 최초 실행 시 API 호출
        if (!apiCalled) {
            callApiAndHandleResult();
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

    // api 호출 메서드 호출 (영화 api)
    private void callApiAndHandleResult() {
        ApiExplorer apiExplorer = new ApiExplorer();
        apiExplorer.fetchAndSaveMoviesToJson(apiKey, listCount, startCount, new ApiExplorer.ApiCallback() {
            @Override // 성공
            public void onSuccess(List<ApiExplorer.SimpleMovie> movies) {
                movieList = movies;
                apiCalled = true;
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "API 호출 성공", Toast.LENGTH_SHORT).show();
                    // 무작위 영화 선택 및 표시
                    displayRandomMovie();
                });
            }

            @Override // 실패
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "API 호출 실패", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            }
        });
    }

    // 영화 랜덤 추천 메서드
    private void displayRandomMovie() {
        if (movieList != null && !movieList.isEmpty()) {
            // 무작위 영화 선택
            Random random = new Random();
            ApiExplorer.SimpleMovie randomMovie = movieList.get(random.nextInt(movieList.size()));

            // UI 업데이트
            titleTextView.setText(randomMovie.getTitle());
            genreTextView.setText(randomMovie.getGenre());
            directorTextView.setText(randomMovie.getDirector());
            plotTextView.setText(randomMovie.getPlot());
        } else {
            Toast.makeText(this, "영화 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // intent 통한 화면 전환
    private void startActivity(Class<?> cls) {
        if (movieList != null) {
            Intent intent = new Intent(MainActivity.this, cls);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "영화 데이터를 가져오는 중입니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // private 데이터 접근 (영화리스트,아이디)
    public static List<ApiExplorer.SimpleMovie> getMovieList(){
        return movieList;
    }
    public static String getUserId(){
        return current_user;
    }
}
