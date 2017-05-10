package org.fossasia.fossasiaorgaandroidapp.model;

/**
 * Created by rishabhkhanna on 25/04/17.
 */

public class LoginDetails {
    String email;
    String password;

    public LoginDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
