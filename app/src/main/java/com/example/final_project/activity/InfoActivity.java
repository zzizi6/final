package com.example.final_project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.final_project.R;
import com.example.final_project.db.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InfoActivity extends AppCompatActivity {


    private TextView textViewName;
    private TextView textViewUserId;
    private TextView textViewGender;
    private TextView textViewAge;

    // SQLite 내부 DB
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        textViewName = findViewById(R.id.textViewName);
        textViewUserId = findViewById(R.id.textViewUserId);
        textViewGender = findViewById(R.id.textViewGender);
        textViewAge = findViewById(R.id.textViewAge);

        dbHelper = new DatabaseHelper(this);

        // 사용자 정보 가져와서 표시
        displayUserInfo();

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

    private void displayUserInfo() {
        // 예시: 현재 사용자 아이디를 기반으로 정보 가져오기
        String userId =  MainActivity.getUserId();// 실제 사용자 아이디 가져오는 로직 필요

        // DatabaseHelper를 사용하여 사용자 정보 가져오기
        String fullName = dbHelper.getFullName(userId); // 예시 메서드, 실제 구현은 DatabaseHelper에 맞게
        String gender = dbHelper.getGender(userId);
        int age = dbHelper.getAge(userId);

        // TextView에 설정
        textViewName.setText("이름: " + fullName);
        textViewUserId.setText("아이디: " + userId);
        textViewGender.setText("성별: " + gender);
        textViewAge.setText("나이: " + age);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close(); // 데이터베이스 연결 해제
        super.onDestroy();
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(InfoActivity.this, cls);
        startActivity(intent);
    }
}
