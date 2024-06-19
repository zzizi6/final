package com.example.final_project.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.db.DatabaseHelper;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUserId;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignUp;

    // SQLite 내부 DB
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // DatabaseHelper 인스턴스 생성
        dbHelper = new DatabaseHelper(this);

        // 뷰 바인딩
        editTextUserId = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // 로그인 버튼 클릭 이벤트 처리
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 사용자명과 비밀번호 가져오기
                String userId = editTextUserId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // 입력값 유효성 검사
                if (TextUtils.isEmpty(userId)) {
                    editTextUserId.setError("아이디를 입력하세요.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("비밀번호를 입력하세요.");
                    return;
                }

                // 사용자 인증 확인
                if (dbHelper.checkUser(userId, password)) {
                    Log.d("LoginActivity", "User credentials are correct");
                    // 로그인 성공 시 메인 화면으로 이동

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("current_user", userId);

                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                } else {
                    // 로그인 실패 시 메시지 표시
                    Toast.makeText(LoginActivity.this, "잘못된 아이디 또는 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원가입 버튼 클릭 이벤트 처리
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUpActivity로 화면 이동
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

            }
        });
    }
}
