package org.fossasia.openevent.app.data.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionResponse {
    public String encryptedEmail;
    public String encryptedPassword;
}
