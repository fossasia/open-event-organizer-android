package org.fossasia.openevent.app.common.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitToken {

    public String token;
    public String password;

    @JsonIgnore
    public String confirmPassword;
}
