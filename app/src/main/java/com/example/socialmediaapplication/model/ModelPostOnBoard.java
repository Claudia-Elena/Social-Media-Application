package com.example.socialmediaapplication.model;


/**
 * Created by LittleDuck
 * Name of project: SocialMediaApplication
 */

public class ModelPostOnBoard {

    String postTime, postComments;
    String title;
    String userDashBoardProfile;
    String userEmail;
    String userId;
    String userImage;
    String description;
    String userName, postLike;

    String pid;

    public ModelPostOnBoard(String postTime, String postComments, String title, String userDashBoardProfile, String userEmail, String userId, String userImage, String description, String userName, String postLike, String profileId) {
        this.postTime = postTime;
        this.postComments = postComments;
        this.title = title;
        this.userDashBoardProfile = userDashBoardProfile;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userImage = userImage;
        this.description = description;
        this.userName = userName;
        this.postLike = postLike;
        this.pid = profileId;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostComments() {
        return postComments;
    }

    public void setPostComments(String postComments) {
        this.postComments = postComments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserDashBoardProfile() {
        return userDashBoardProfile;
    }

    public void setUserDashBoardProfile(String userDashBoardProfile) {
        this.userDashBoardProfile = userDashBoardProfile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostLike() {
        return postLike;
    }

    public void setPostLike(String postLike) {
        this.postLike = postLike;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}

