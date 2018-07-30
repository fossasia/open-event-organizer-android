package com.eventyay.organizer.data.encryption;


public interface EncryptionService {

    String encrypt(String credential);

    String decrypt(String encryptedCredentials);

}
