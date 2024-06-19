package com.example.final_project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String title;
    private String genre;
    private String director;
    private String releaseDate;
    private String plot; // 줄거리
    private String posterUrl; // 포스터 URL

    public Movie(String title, String genre, String director, String releaseDate, String plot, String posterUrl) {
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.releaseDate = releaseDate;
        this.plot = plot;
        this.posterUrl = posterUrl;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        genre = in.readString();
        director = in.readString();
        releaseDate = in.readString();
        plot = in.readString();
        posterUrl = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
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
        dest.writeString(plot);
        dest.writeString(posterUrl);
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

    public String getPosterUrl() {
        return posterUrl;
    }
}
