package org.fossasia.openevent.app.common.data.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Login {
    public String email;
    public String password;
}
