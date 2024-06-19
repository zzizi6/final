package com.example.final_project.api;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiExplorer {

    private static final String TAG = "ApiExplorer";
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    // 비동기로 API 요청을 수행하고 결과를 콜백으로 전달
    public void fetchAndSaveMoviesToJson(String apiKey, int listCount, int startCount, ApiCallback callback) {
        Future<String> future = executorService.submit(() -> fetchMovies(apiKey, listCount, startCount));

        executorService.execute(() -> {
            try {
                String jsonResponse = future.get(); // 비동기 작업 완료까지 대기
                List<SimpleMovie> movies = extractRelevantData(jsonResponse);
                if (callback != null) {
                    callback.onSuccess(movies);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching movies", e);
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    // API에서 데이터를 가져오는 메서드
    private String fetchMovies(String apiKey, int listCount, int startCount) throws IOException {
        HttpURLConnection conn = null;
        StringBuilder response = new StringBuilder();

        try {
            // API 요청 URL 설정
            String baseUrl = "https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp";
            String encodedApiKey = URLEncoder.encode(apiKey, "UTF-8");
            @SuppressLint("DefaultLocale") String urlStr = String.format("%s?collection=kmdb_new2&nation=대한민국&ServiceKey=%s&listCount=%d&startCount=%d&releaseDts=20000101",
                    baseUrl, encodedApiKey, listCount, startCount);

            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Log.d(TAG, "Request URL: " + urlStr);
            Log.d(TAG, "Response code: " + conn.getResponseCode());

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            try (BufferedReader rd = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return response.toString();
    }

    // JSON 응답에서 필요한 데이터를 추출하여 SimpleMovie 객체 목록으로 변환
    private List<SimpleMovie> extractRelevantData(String jsonResponse) {
        Gson gson = new GsonBuilder().create();
        KMDBResponse response = gson.fromJson(jsonResponse, KMDBResponse.class);
        List<SimpleMovie> simpleMovies = new ArrayList<>();

        if (response != null && response.data != null) {
            for (KMDBResponse.Data movieData : response.data) {
                if (movieData.result != null) {
                    for (KMDBResponse.Data.Result result : movieData.result) {
                        SimpleMovie simpleMovie = new SimpleMovie();
                        simpleMovie.title = result.title.trim();
                        simpleMovie.genre = result.genre.trim();
                        simpleMovie.releaseDate = result.repRlsDate.trim(); // 개봉일 정보
                        simpleMovie.postUrl = result.posters; // 포스터 URL
                        simpleMovie.plot = result.plots != null && result.plots.plot != null && result.plots.plot.size() > 0
                                ? result.plots.plot.get(0).plotText : null; // 줄거리 정보

                        // 감독 정보는 KMDB API에서 directorNm으로 되어 있으며, 여러 명일 수 있음
                        if (result.directors != null && result.directors.director != null && result.directors.director.size() > 0) {
                            simpleMovie.director = result.directors.director.get(0).directorNm;
                        }

                        simpleMovies.add(simpleMovie);
                    }
                }
            }
        }

        return simpleMovies;
    }

    // 콜백 인터페이스 정의
    public interface ApiCallback {
        void onSuccess(List<SimpleMovie> movies);

        void onFailure(Exception e);
    }

    // SimpleMovie 클래스 정의
    public static class SimpleMovie implements Parcelable {
        String title;
        String genre;
        String director;
        String releaseDate;
        String postUrl;
        String plot;

        public SimpleMovie() {
        }

        protected SimpleMovie(Parcel in) {
            title = in.readString();
            genre = in.readString();
            director = in.readString();
            releaseDate = in.readString();
            postUrl = in.readString();
            plot = in.readString();
        }

        // Getter 메서드들
        public String getTitle() {
            return title;
        }

        public String getGenre() {
            return genre;
        }

        public String getDirector() {
            return director;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getPlot() {
            return plot;
        }

        public String getPostUrl() {
            return postUrl;
        }



        public static final Creator<SimpleMovie> CREATOR = new Creator<SimpleMovie>() {
            @Override
            public SimpleMovie createFromParcel(Parcel in) {
                return new SimpleMovie(in);
            }

            @Override
            public SimpleMovie[] newArray(int size) {
                return new SimpleMovie[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(genre);
            dest.writeString(director);
            dest.writeString(releaseDate);
            dest.writeString(postUrl);
            dest.writeString(plot);
        }
    }

    // KMDB API의 JSON 응답에 맞춘 데이터 구조 정의
    private static class KMDBResponse {
        @SerializedName("Data")
        List<Data> data;

        static class Data {
            @SerializedName("Result")
            List<Result> result;

            static class Result {
                @SerializedName("title")
                String title;
                @SerializedName("genre")
                String genre;
                @SerializedName("repRlsDate")
                String repRlsDate; // 대표 개봉일 정보
                @SerializedName("directors")
                Directors directors;
                @SerializedName("posters")
                String posters; // 포스터 URL 정보
                @SerializedName("plots")
                Plots plots; // 줄거리 정보

                static class Directors {
                    @SerializedName("director")
                    List<Director> director;

                    static class Director {
                        @SerializedName("directorNm")
                        String directorNm;
                    }
                }

                static class Plots {
                    @SerializedName("plot")
                    List<Plot> plot;

                    static class Plot {
                        @SerializedName("plotText")
                        String plotText;
                    }
                }
            }
        }
    }
}
