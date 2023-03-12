package com.example.socialmediaapplication.model;

public class ModelPostOnBoard {

    String postTime, postComments;
    String title;
    String udp;
    String userEmail;
    String userId;
    String userImage;
    String description;
    String userName, postLike;

    public ModelPostOnBoard(String postTime, String postComments, String title, String udp, String userEmail, String userId, String userImage, String description, String userName, String postLike) {
        this.postTime = postTime;
        this.postComments = postComments;
        this.title = title;
        this.udp = udp;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userImage = userImage;
        this.description = description;
        this.userName = userName;
        this.postLike = postLike;
    }


}

