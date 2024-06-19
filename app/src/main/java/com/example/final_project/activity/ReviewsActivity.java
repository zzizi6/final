package com.example.final_project.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.adapter.ReviewsAdapter;
import com.example.final_project.db.DatabaseHelper;
import com.example.final_project.model.Review;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewsAdapter adapter;
    private List<Review> reviewList;
    private DatabaseHelper dbHelper;
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        dbHelper = new DatabaseHelper(this);

        // 검색어 입력란 초기화
        editTextSearch = findViewById(R.id.editTextSearch);

        // 리뷰 목록 초기화
        recyclerView = findViewById(R.id.recyclerViewReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리뷰 데이터 불러오기 (초기화면은 전체 리뷰)
        loadAllReviews();

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

    // 전체 리뷰를 데이터베이스에서 불러와서 RecyclerView에 설정하는 메서드
    private void loadAllReviews() {
        reviewList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_RATINGS, null, null, null,
                null, null, null);

        while (cursor.moveToNext()) {
            String movieTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOVIE_TITLE));
            String writer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WRITER));
            float rating = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING));
            String reviewText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REVIEW));

            // 읽어온 데이터를 Review 객체로 만들어 리스트에 추가합니다.
            reviewList.add(new Review(movieTitle, writer, rating, reviewText));
        }

        cursor.close();
        db.close();

        // 리사이클러뷰에 어댑터 설정
        adapter = new ReviewsAdapter(reviewList);
        recyclerView.setAdapter(adapter);
    }

    // 검색 버튼 클릭 시 호출되는 메서드
    public void performSearch(View view) {
        String searchQuery = editTextSearch.getText().toString().trim();

        if (TextUtils.isEmpty(searchQuery)) {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewList.clear(); // 검색 결과를 표시하기 위해 기존 목록 초기화

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COLUMN_MOVIE_TITLE + " LIKE ?";
        String[] selectionArgs = {"%" + searchQuery + "%"};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USER_RATINGS, null, selection, selectionArgs,
                null, null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String dramaTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOVIE_TITLE));
            @SuppressLint("Range") String writer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_WRITER));
            @SuppressLint("Range") float rating = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_RATING));
            @SuppressLint("Range") String reviewText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REVIEW));
            reviewList.add(new Review(dramaTitle, writer, rating, reviewText));
        }

        cursor.close();
        db.close();

        if (reviewList.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // 리사이클러뷰에 변경된 데이터 반영
        adapter.notifyDataSetChanged();
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(ReviewsActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
