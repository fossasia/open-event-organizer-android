package org.fossasia.openevent.app.data.auth;

import android.os.Build;

import org.fossasia.openevent.app.data.auth.model.Encryption;
import org.fossasia.openevent.app.utils.EncryptionUtils;

import javax.inject.Inject;

public class EncryptionServiceImpl implements EncryptionService {

    private AuthHolder authHolder;

    @Inject
    public EncryptionServiceImpl(AuthHolder authHolder) {
        this.authHolder = authHolder;
    }

    @Override
    public void setEncryption(Encryption encryption) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            authHolder.saveEncryptedEmail(EncryptionUtils.encryptString(encryption.getContext(), encryption.getEmail()));
            authHolder.saveEncryptedPassword(EncryptionUtils.encryptString(encryption.getContext(), encryption.getPassword()));
        } else {
            authHolder.saveEncryptedEmail(encryption.getEmail());
            authHolder.saveEncryptedPassword(encryption.getPassword());
        }
    }
}
