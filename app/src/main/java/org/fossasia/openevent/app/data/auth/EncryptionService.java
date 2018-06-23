package org.fossasia.openevent.app.data.auth;

import org.fossasia.openevent.app.data.auth.model.Login;

public interface EncryptionService {

    void setEncryption(Login login);

    String getEmail();

    String getUserpassword();
}
