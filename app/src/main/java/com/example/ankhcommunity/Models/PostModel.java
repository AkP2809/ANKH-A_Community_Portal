package com.example.ankhcommunity.Models;

import com.google.firebase.database.ServerValue;

public class PostModel {

    private String postKey;
    private String complainTitle;
    private String complainCategory;
    private String complainDescription;
    private String complainPicture;
    private String userID;
    private String userPhoto;
    private Object timeStamp;

    public PostModel(String complainTitle, String complainCategory, String complainDescription, String complainPicture, String userID, String userPhoto) {
        this.complainTitle = complainTitle;
        this.complainCategory = complainCategory;
        this.complainDescription = complainDescription;
        this.complainPicture = complainPicture;
        this.userID = userID;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public PostModel() {

    }

    public String getPostKey() {
        return postKey;
    }

    public String getComplainTitle() {
        return complainTitle;
    }

    public void setComplainTitle(String complainTitle) {
        this.complainTitle = complainTitle;
    }

    public String getComplainCategory() {
        return complainCategory;
    }

    public void setComplainCategory(String complainCategory) {
        this.complainCategory = complainCategory;
    }

    public String getComplainDescription() {
        return complainDescription;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public void setComplainDescription(String complainDescription) {
        this.complainDescription = complainDescription;
    }

    public String getComplainPicture() {
        return complainPicture;
    }

    public void setComplainPicture(String complainPicture) {
        this.complainPicture = complainPicture;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
