package org.fossasia.openevent.app.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    @JsonProperty("access_token")
    public String accessToken;

    public LoginResponse() {}
}
