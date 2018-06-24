package org.fossasia.openevent.app.data.auth;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
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

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import timber.log.Timber;

public class EncryptionServiceImpl implements EncryptionService {

    private Context context;
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String ALIAS = "MY_APP";
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
            return encryptString(context, credential);
        } else {
            return credential;
        }
    }

    @Override
    public String decrypt(String encryptedCredentials) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return decryptString(context, encryptedCredentials);
        } else {
            return encryptedCredentials;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String encryptString(Context context, String toEncrypt) {
        try {
            final KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKey(context);
            if (privateKeyEntry != null) {
                final PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

                // Encrypt the text
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
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String decryptString(Context context, String encrypted) {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = getPrivateKey(context);
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
    private KeyStore.PrivateKeyEntry getPrivateKey(Context context) throws KeyStoreException,
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
                createKeys(context);

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
    private void createKeys(Context context) throws NoSuchProviderException,
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
}

