package com.example.final_project.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름과 버전
    private static final String DATABASE_NAME = "UserRating.db";
    private static final int DATABASE_VERSION = 1;

    // (1) 사용자 테이블 관련 상수
    public static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_PASSWORD = "password";
    private static final String COL_GENDER = "gender";
    private static final String COL_AGE = "age";

    // (1) 사용자 테이블 생성 쿼리
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_FULL_NAME + " TEXT,"
            + COL_USER_ID + " TEXT,"
            + COL_PASSWORD + " TEXT,"
            + COL_GENDER + " TEXT,"
            + COL_AGE + " INTEGER"
            + ")";

    // (2) 즐겨찾기 테이블 관련 상수
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_FAVORITE_TITLE = "title";

    // (2) 즐겨찾기 테이블 생성 쿼리
    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " + TABLE_FAVORITES + " ("
            + COLUMN_FAVORITE_TITLE + " TEXT PRIMARY KEY)";

    // (3) 영화 평점 및 리뷰 테이블 관련 상수
    public static final String TABLE_USER_RATINGS = "user_ratings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MOVIE_TITLE = "movie_title";
    public static final String COLUMN_WRITER = "writer";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_REVIEW = "review";

    // (3) 영화 평점 및 리뷰 테이블 생성 쿼리
    private static final String SQL_CREATE_TABLE_USER_RATINGS =
            "CREATE TABLE " + TABLE_USER_RATINGS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MOVIE_TITLE + " TEXT," +
                    COLUMN_WRITER + " WRITER," +
                    COLUMN_RATING + " REAL," +
                    COLUMN_REVIEW + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FAVORITES); // 즐겨찾기 테이블 생성
        db.execSQL(SQL_CREATE_TABLE_USER_RATINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 업그레이드 시 기존 테이블 삭제 후 재생성
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES); // 즐겨찾기 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_RATINGS);
        onCreate(db);
    }

    // 회원 등록 메서드
    public boolean registerUser(String fullName, String userId, String password, String gender, int age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FULL_NAME, fullName);
        values.put(COL_USER_ID, userId);
        values.put(COL_PASSWORD, password);
        values.put(COL_GENDER, gender);
        values.put(COL_AGE, age);

        long rowId = db.insert(TABLE_USERS, null, values);
        db.close();

        return rowId != -1;
    }

    // 로그인 검증 메서드
    public boolean checkUser(String userId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = { COL_ID };
        String selection = COL_USER_ID + "=? AND " + COL_PASSWORD + "=?";
        String[] selectionArgs = { userId, password };

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    // 영화 제목 즐겨찾기 목록에 추가
    public boolean addFavoriteMovie(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE_TITLE, title);

        // 중복되지 않게 추가하기 위해 'PRIMARY KEY' 제약조건 활용
        long result = db.insertWithOnConflict(TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return result != -1; // -1이면 이미 존재
    }

    // 모든 즐겨찾기 영화 제목을 가져오기
    public Cursor getAllFavoriteMovie() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES;
        return db.rawQuery(query, null);
    }

    // 즐겨찾기 에서 영화 제거
    public boolean removeFavoriteMovie(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_FAVORITES, COLUMN_FAVORITE_TITLE + "=?", new String[]{title});
        db.close();
        return deletedRows > 0;
    }

    @SuppressLint("Range")
    public String getFullName(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = null;
        String query = "SELECT " + COL_FULL_NAME + " FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndex(COL_FULL_NAME));
        }
        cursor.close();
        return fullName;
    }

    @SuppressLint("Range")
    public String getGender(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String gender = null;
        String query = "SELECT " + COL_GENDER + " FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        if (cursor.moveToFirst()) {
            gender = cursor.getString(cursor.getColumnIndex(COL_GENDER));
        }
        cursor.close();
        return gender;
    }

    @SuppressLint("Range")
    public int getAge(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int age = -1;
        String query = "SELECT " + COL_AGE + " FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        if (cursor.moveToFirst()) {
            age = cursor.getInt(cursor.getColumnIndex(COL_AGE));
        }
        cursor.close();
        return age;
    }

    // 영화가 즐겨찾기 목록에 있는지 여부를 반환
    public boolean isFavoriteMovie(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_FAVORITE_TITLE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{title});
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }
}
