package org.fossasia.openevent.app.data.models;


import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    public LoginResponse() {}

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
            "accessToken='" + accessToken + '\'' +
            '}';
    }
}
