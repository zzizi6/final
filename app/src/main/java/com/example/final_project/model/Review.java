package com.example.final_project.model;

public class Review {
    private String title;  // 영화 제목
    private String writer; // 리뷰 작성한 유저 이름
    private float rating;     // 평점
    private String review;    // 리뷰 내용

    public Review(String title,String writer, float rating, String review) {
        this.title = title;
        this.writer= writer;
        this.rating = rating;
        this.review = review;
    }

    public String getTitle() {
        return title;
    }
    public String getWriter(){
        return writer;
    }
    public float getRating() {
        return rating;
    }
    public String getReview() {
        return review;
    }
}
