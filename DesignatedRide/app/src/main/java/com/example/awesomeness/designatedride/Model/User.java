package com.example.awesomeness.designatedride.Model;

/**
 * Created by awesomeness on 2/1/18.
 */

public class User {

    public String userId;
    public String userMode;
    public String userFirstname;
    public String userLastname;
    public String userEmail;
    public String userImage;

    public User() {
    }

    public User(String userId, String userMode, String userFirstname,
                String userLastname, String userEmail) {
        this.userId = userId;
        this.userMode = userMode;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userImage = userImage;
    }

    public User(String userId, String userMode, String userFirstname,
                String userLastname, String userEmail, String userImage) {
        this.userId = userId;
        this.userMode = userMode;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.userEmail = userEmail;
        this.userImage = userImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMode() {
        return userMode;
    }

    public void setUserMode(String userMode) {
        this.userMode = userMode;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getUserLastname() {
        return userLastname;
    }

    public void setUserLastname(String userLastname) {
        this.userLastname = userLastname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

}
