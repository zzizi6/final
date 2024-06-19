package com.example.final_project.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.example.final_project.db.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextUserId;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private RadioGroup radioGroupGender;
    private EditText editTextAge;
    private Button buttonSignUp;

    // SQLite 내부 DB
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // DatabaseHelper 인스턴스 생성
        dbHelper = new DatabaseHelper(this);

        // 뷰 바인딩
        editTextName = findViewById(R.id.editTextFullName);
        editTextUserId = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        editTextAge = findViewById(R.id.editTextAge);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // 회원가입 버튼 클릭 이벤트 처리
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String userName = editTextName.getText().toString().trim();
        String userId = editTextUserId.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGenderRadioButton = findViewById(selectedGenderId);
        String gender = selectedGenderRadioButton.getText().toString(); // 선택된 성별
        int age = Integer.parseInt(editTextAge.getText().toString().trim());

        // 입력값 유효성 검사
        if (TextUtils.isEmpty(userName)) {
            editTextName.setError("성명을 입력하세요.");
            return;
        }

        if (TextUtils.isEmpty(userId)) {
            editTextUserId.setError("아이디를 입력하세요.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("비밀번호를 입력하세요.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("비밀번호가 일치하지 않습니다.");
            return;
        }

        if (selectedGenderId == -1) { // 선택된 성별이 없는 경우
            Toast.makeText(this, "성별을 선택하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 회원 정보 저장
        boolean isSuccess = dbHelper.registerUser(userName, userId, password, gender, age);

        if (isSuccess) {
            // 회원가입 성공 시 메시지 표시 및 로그인 화면으로 이동
            Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
            finish(); // SignUpActivity 종료
        } else {
            // 회원가입 실패 시 메시지 표시
            Toast.makeText(this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
