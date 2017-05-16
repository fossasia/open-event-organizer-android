package org.fossasia.openevent.app.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    private int id;
    @SerializedName("last_access_time")
    private String lastAccessTime;
    @SerializedName("signup_time")
    private String signupTime;
    @SerializedName("user_detail")
    private UserDetail userDetail;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(String signupTime) {
        this.signupTime = signupTime;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
