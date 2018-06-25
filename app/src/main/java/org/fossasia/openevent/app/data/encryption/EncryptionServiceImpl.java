package org.fossasia.openevent.app.data.encryption;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import timber.log.Timber;

public class EncryptionServiceImpl implements EncryptionService {

    private final Context context;
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String ALIAS = "openevent_orga";
    private static final String TYPE_RSA = "RSA";
    private static final String CYPHER = "RSA/ECB/PKCS1Padding";
    private static final String ENCODING = "UTF-8";

    @Inject
    public EncryptionServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public String encrypt(String credential) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return encryptString(credential);
        } else {
            SecretKey secretKey = generateKey(ALIAS);
            byte[] byteArray = encryptMsg(credential, secretKey);
            String encryptedString = new String(byteArray);
            return encryptedString;
        }
    }

    @Override
    public String decrypt(String encryptedCredentials) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return decryptString(encryptedCredentials);
        } else {
            return decryptMsg(encryptedCredentials.getBytes(), generateKey(ALIAS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String encryptString(String toEncrypt) {
        if (toEncrypt != null) {
            try {
                final KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKey();
                if (privateKeyEntry != null) {
                    final PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();
                    Cipher input = Cipher.getInstance(CYPHER);
                    input.init(Cipher.ENCRYPT_MODE, publicKey);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
                    cipherOutputStream.write(toEncrypt.getBytes(ENCODING));
                    cipherOutputStream.close();
                    byte[] vals = outputStream.toByteArray();
                    return Base64.encodeToString(vals, Base64.DEFAULT);
                }
            } catch (Exception e) {
                Timber.e(Log.getStackTraceString(e));
                return null;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String decryptString(String encrypted) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKey();
            if (privateKeyEntry != null) {
                final PrivateKey privateKey = privateKeyEntry.getPrivateKey();

                Cipher output = Cipher.getInstance(CYPHER);
                output.init(Cipher.DECRYPT_MODE, privateKey);

                CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(encrypted, Base64.DEFAULT)), output);
                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }

                byte[] bytes = new byte[values.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = values.get(i);
                }

                return new String(bytes, 0, bytes.length, ENCODING);
            }

        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
            return null;
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private KeyStore.PrivateKeyEntry getPrivateKey() throws KeyStoreException,
        CertificateException, NoSuchAlgorithmException,
        IOException, UnrecoverableEntryException {

        KeyStore ks = KeyStore.getInstance(KEYSTORE);

        ks.load(null);

        // Load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(ALIAS, null);

        //If the entry is null, keys were never stored under this alias.
        if (entry == null) {
            Timber.w("No key found under alias: " + ALIAS);
            Timber.w("Generating new key...");
            try {
                createKeys();

                // reload keystore
                ks = KeyStore.getInstance(KEYSTORE);
                ks.load(null);

                // reload key pair
                entry = ks.getEntry(ALIAS, null);

                if (entry == null) {
                    Timber.w("Generating new key failed...");
                    return null;
                }
            } catch (NoSuchProviderException e) {
                Timber.w("Generating new key failed...");
                e.printStackTrace();
                return null;
            } catch (InvalidAlgorithmParameterException e) {
                Timber.w("Generating new key failed...");
                e.printStackTrace();
                return null;
            }
        }

        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Timber.w("Not an instance of a PrivateKeyEntry");
            Timber.w("Exiting signData()...");
            return null;
        }

        return (KeyStore.PrivateKeyEntry) entry;
    }

    /**
     * Creates a public and private key and stores it using the Android Key Store, so that only
     * this application will be able to access the keys.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void createKeys() throws NoSuchProviderException,
        NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 25);

        KeyPairGeneratorSpec spec =
            new KeyPairGeneratorSpec.Builder(context)
                // You'll use the alias later to retrieve the key.
                .setAlias(ALIAS)
                // The subject used for the self-signed certificate of the generated pair
                .setSubject(new X500Principal("CN=" + ALIAS))
                // The serial number used for the self-signed certificate of the
                // generated pair.
                .setSerialNumber(BigInteger.valueOf(1337))
                // Date range of validity for the generated pair.
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();

        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
        // and the KeyStore.  This example uses the AndroidKeyStore.
        final KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(TYPE_RSA, KEYSTORE);
        kpGenerator.initialize(spec);

        final KeyPair kp = kpGenerator.generateKeyPair();
        Timber.d("Public Key is: " + kp.getPublic().toString());
    }

    /*
    * The following methods are specifically taken into use when the API level is
    * less than 18
    *
    */
     private byte[] encryptMsg(String message, SecretKey secret) {

     Cipher cipher;
     byte[] cipherText = new byte[0];

     if (message != null) {
         try {
             cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
             cipher.init(Cipher.ENCRYPT_MODE, secret);
             cipherText = cipher.doFinal(message.getBytes("UTF-8"));
         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
         } catch (NoSuchPaddingException e) {
             e.printStackTrace();
         } catch (InvalidKeyException e) {
             e.printStackTrace();
         } catch (IllegalBlockSizeException e) {
             e.printStackTrace();
         } catch (BadPaddingException e) {
             e.printStackTrace();
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
         return cipherText;
     }
        return null;
     }

    private String decryptMsg(byte[] cipherText, SecretKey secret) {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        String decryptString = null;

        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return decryptString;
    }

    private SecretKey generateKey(String password) {
        return new SecretKeySpec(password.getBytes(), "AES");
    }

}

