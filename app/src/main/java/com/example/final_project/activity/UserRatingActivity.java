package com.example.final_project.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.R;
import com.example.final_project.api.ApiExplorer;
import com.example.final_project.db.DatabaseHelper;
import com.example.final_project.model.Movie;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class UserRatingActivity extends AppCompatActivity {

    private TextView textViewDramaTitle;
    private RatingBar ratingBar;
    private EditText editTextReview;
    private Button buttonSave;
    private List<Movie> movieList;

    // SQLite 내부 DB
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rating);

        // 드라마 제목 설정
        textViewDramaTitle = findViewById(R.id.textViewMovieTitle);

        // SQLite 데이터베이스 Helper 초기화
        dbHelper = new DatabaseHelper(this);

        // MainActivity에서 가져온 movieList 사용
        if (MainActivity.getMovieList() != null) {
            movieList = convertMovieListToDramaList(MainActivity.getMovieList());
        } else {
            Log.e("UserRatingActivity", "MainActivity.movieList is null or empty");
            movieList = new ArrayList<>(); // 예외 처리: movieList가 null일 때 빈 리스트로 초기화
        }

        // 별점을 입력받는 RatingBar 초기화
        ratingBar = findViewById(R.id.ratingBar);

        // 간단한 리뷰를 입력받는 EditText 초기화
        editTextReview = findViewById(R.id.editTextReview);

        // 저장 버튼 초기화
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> saveRatingAndReview());

        // 검색 버튼 초기화
        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(v -> performSearch());

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

    private void performSearch() {
        // 검색어를 가져옴
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        String searchQuery = editTextSearch.getText().toString().trim();

        // 검색어가 비어있는지 확인
        if (TextUtils.isEmpty(searchQuery)) {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // movieList가 비어있는지 확인
        if (movieList == null || movieList.isEmpty()) {
            Log.e("UserRatingActivity", "movieList is null or empty");
            Toast.makeText(this, "영화 목록이 비어 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // movieList에서 검색어와 동일한 영화가 있는지 확인
        boolean movieExists = false;
        for (Movie movie : movieList) {
            Log.e("UserRatingActivity",movie.getTitle());
            if (movie.getTitle().equalsIgnoreCase(searchQuery)) {
                movieExists = true;
                textViewDramaTitle.setText(searchQuery); // 일치하는 영화 제목을 TextView에 설정
                break;
            }
        }

        // 검색어와 동일한 영화가 없을 경우 Toast 메시지 출력
        if (!movieExists) {
            textViewDramaTitle.setText(""); // 일치하는 영화가 없으면 제목을 비움
            Toast.makeText(this, "입력한 영화명과 일치하는 영화가 없습니다. => " + searchQuery, Toast.LENGTH_SHORT).show();
            Log.e("UserRatingActivity", "No movie found with title: " + searchQuery);
        }
    }

    private void saveRatingAndReview() {

        // 현재 사용자의 ID를 SharedPreferences에서 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null); // default value is null

        // 별점과 리뷰 내용을 가져오는 예시 코드
        float rating = ratingBar.getRating();
        String review = editTextReview.getText().toString();
        String movieTitle = textViewDramaTitle.getText().toString();
        String writer = userId;

        // 데이터베이스에 저장
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MOVIE_TITLE, movieTitle);
        values.put(DatabaseHelper.COLUMN_WRITER, writer);
        values.put(DatabaseHelper.COLUMN_RATING, rating);
        values.put(DatabaseHelper.COLUMN_REVIEW, review);

        // 삽입 작업 실행
        long newRowId = db.insert(DatabaseHelper.TABLE_USER_RATINGS, null, values);

        if (newRowId == -1) {
            // 삽입 실패
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        } else {
            // 삽입 성공
            Toast.makeText(this, "별점과 리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // MainActivity에서 가져온 SimpleMovie 리스트를 Drama 객체 리스트로 변환하는 메서드
    private List<Movie> convertMovieListToDramaList(List<ApiExplorer.SimpleMovie> movieList) {
        List<Movie> dramaList = new ArrayList<>();
        for (ApiExplorer.SimpleMovie simpleMovie : movieList) {
            Movie movie = new Movie(
                    simpleMovie.getTitle(),
                    simpleMovie.getGenre(),
                    simpleMovie.getDirector(),
                    simpleMovie.getReleaseDate(),
                    simpleMovie.getPlot(),
                    simpleMovie.getPostUrl()
            );
            dramaList.add(movie);
        }
        return dramaList;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close(); // 데이터베이스 연결 해제
        super.onDestroy();
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(UserRatingActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
