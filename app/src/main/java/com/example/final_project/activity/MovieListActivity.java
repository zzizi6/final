package com.example.final_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.adapter.MovieAdapter;
import com.example.final_project.api.ApiExplorer;
import com.example.final_project.db.DatabaseHelper;
import com.example.final_project.model.Movie;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private List<Movie> originalMovieList; // 원본 리스트 복원용
    private EditText editTextSearch;
    private Button buttonSearch;
    private RadioButton radioButtonFavorites;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        dbHelper = new DatabaseHelper(this);

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerViewDramas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 검색 UI 초기화
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        radioButtonFavorites = findViewById(R.id.radioButtonFavorite);

        // MainActivity에서 가져온 movieList 사용
        if (MainActivity.getMovieList() != null) {
            movieList = convertMovieListToDramaList(MainActivity.getMovieList());
            originalMovieList = new ArrayList<>(movieList); // 원본 리스트 복원용
            adapter = new MovieAdapter(movieList, this, this); // Context 전달
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("MovieListActivity", "movieList is null");
        }

        // 검색 버튼 클릭 리스너 설정
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString();
                filterAndSearch(searchText);
            }
        });

        // 라디오 버튼 클릭 리스너 설정
        radioButtonFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // 즐겨찾기 영화만 표시
                filterAndSearch(editTextSearch.getText().toString());
            } else {
                // 모든 영화 표시
                adapter.setMovieList(originalMovieList); // 원본 리스트 복원
            }
        });

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId(); // 메뉴 아이템의 id 가져오기

            if (itemId == R.id.page_home) {
                startActivity(MainActivity.class);
                return true;
            } else if (itemId == R.id.page_movie_list) {
                startActivity(MovieListActivity.class);
                return true;
            } else if (itemId == R.id.page_movie_review) {
                startActivity(ReviewsActivity.class);
                return true;
            } else if (itemId == R.id.page_user_rating) {
                startActivity(UserRatingActivity.class);
                return true;
            } else if (itemId == R.id.page_info) {
                startActivity(InfoActivity.class);
                return true;
            }

            return false;
        });
    }

    @Override
    public void onItemClick(int position) {
        // 해당 영화의 상세 화면으로 이동하는 메서드
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", movieList.get(position)); // Movie 객체를 직접 전달
        startActivity(intent);
    }

    // MainActivity에서 가져온 SimpleMovie 리스트를 Movie 객체 리스트로 변환하는 메서드
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

    // 즐겨찾기 필터링 및 검색 기능 구현
    private void filterAndSearch(String searchText) {
        List<Movie> filteredList = new ArrayList<>();
        if (radioButtonFavorites.isChecked()) {
            // 즐겨찾기 필터링
            for (Movie movie : originalMovieList) {
                if (dbHelper.isFavoriteMovie(movie.getTitle())) { // 즐겨찾기에 있는지 확인
                    filteredList.add(movie);
                }
            }
        } else {
            // 필터 없이 전체 리스트 사용
            filteredList.addAll(originalMovieList);
        }

        // 검색어로 필터링
        if (!searchText.isEmpty()) {
            List<Movie> searchResults = new ArrayList<>();
            for (Movie movie : filteredList) {
                if (movie.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    searchResults.add(movie);
                }
            }
            filteredList = searchResults;
        }

        // RecyclerView 업데이트
        adapter.setMovieList(filteredList);
    }

    // 하트 버튼 클릭 시 호출되는 메서드 (즐겨찾기 추가 또는 제거)
    public void onHeartButtonClick(View view) {
        int position = recyclerView.getChildLayoutPosition((View) view.getParent().getParent()); // 버튼이 속한 아이템의 위치
        Movie movie = originalMovieList.get(position);

        // 즐겨찾기 상태 토글
        if (dbHelper.isFavoriteMovie(movie.getTitle())) {
            dbHelper.removeFavoriteMovie(movie.getTitle());
            Log.d("MovieListActivity", movie.getTitle() + " 즐겨찾기에서 제거됨");
        } else {
            dbHelper.addFavoriteMovie(movie.getTitle());
            Log.d("MovieListActivity", movie.getTitle() + " 즐겨찾기에 추가됨");
        }

        // 현재 필터에 맞춰 리스트 갱신
        filterAndSearch(editTextSearch.getText().toString());
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(MovieListActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
