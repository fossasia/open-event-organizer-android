package org.fossasia.openevent.app.data.auth;

import android.content.Context;
import android.os.Build;

import org.fossasia.openevent.app.data.auth.model.Login;
import org.fossasia.openevent.app.utils.EncryptionUtils;

import javax.inject.Inject;

public class EncryptionServiceImpl implements EncryptionService {

    private AuthHolder authHolder;

    @Inject
    Context context;

    @Inject
    public EncryptionServiceImpl(AuthHolder authHolder) {
        this.authHolder = authHolder;
    }

    @Override
    public void setEncryption(Login login) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            authHolder.saveEncryptedEmail(EncryptionUtils.encryptString(context, login.getEmail()));
            authHolder.saveEncryptedPassword(EncryptionUtils.encryptString(context, login.getPassword()));
        } else {
            authHolder.saveEncryptedEmail(login.getEmail());
            authHolder.saveEncryptedPassword(login.getPassword());
        }
    }

    @Override
    public String getEmail() {
        String email;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            email = EncryptionUtils.decryptString(context, authHolder.getEmail());
        } else {
            email = authHolder.getEmail();
        }
        return email;
    }

    @Override
    public String getUserpassword() {
        String password;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            password = EncryptionUtils.decryptString(context, authHolder.getPassword());
        } else {
            password = authHolder.getPassword();
        }
        return password;
    }
}
