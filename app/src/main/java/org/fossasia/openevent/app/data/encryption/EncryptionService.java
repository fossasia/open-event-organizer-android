package org.fossasia.openevent.app.data.encryption;


public interface EncryptionService {

    String encrypt(String credential);

    String decrypt(String encryptedCredentials);

}
