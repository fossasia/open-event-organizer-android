package org.fossasia.openevent.app.data.auth;


public interface EncryptionService {

    String encrypt(String credential);

    String decrypt(String encryptedCredentials);

}
