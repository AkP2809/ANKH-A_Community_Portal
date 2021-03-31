package com.example.ankhcommunity.Models;

import com.google.firebase.database.ServerValue;

public class CommentModel {
    private String content, userID, userPhoto, userName;
    private Object timeStamp;

    public CommentModel() {
    }

    public CommentModel(String content, String userID, String userPhoto, String userName) {
        this.content = content;
        this.userID = userID;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public CommentModel(String content, String userID, String userPhoto, String userName, Object timeStamp) {
        this.content = content;
        this.userID = userID;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
