package org.fossasia.openevent.app.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Login {
    public String email;
    public String password;
}
