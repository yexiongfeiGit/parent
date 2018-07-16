package com.wokoworks.framework.commons.encrypt;

import lombok.Data;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author 0x0001
 */
public class RSAEncryption {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public RSAEncryption(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public byte[] encrypt(byte[] rawData) {
        return process(publicKey, rawData);
    }

    public byte[] decrypt(byte[] encryptData) {
        return process(privateKey, encryptData);
    }

    private byte[] process(RSAKey key, byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            int size = (key.getModulus().bitLength() + 7) / 8;

            if (key instanceof PrivateKey) {
                cipher.init(Cipher.DECRYPT_MODE, ((PrivateKey) key));
            } else if (key instanceof PublicKey) {
                cipher.init(Cipher.ENCRYPT_MODE, (PublicKey) key);
                size -= 11;
            } else {
                throw new IllegalArgumentException("argument error");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i = 0; i < bytes.length / size; i++) {
                byte[] data = cipher.doFinal(bytes, i * size, size);
                out.write(data);
            }

            int b = bytes.length % size;
            if (b > 0) {
                byte[] data = cipher.doFinal(bytes, bytes.length - b, b);
                out.write(data);
            }


            return out.toByteArray();
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // tools methods

    public static RSAPublicKey generatePublicKey(byte[] publicKey) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(publicKey);
        return (RSAPublicKey) keyFactory.generatePublic(pubSpec);
    }

    public static RSAPrivateKey generatePrivateKey(byte[] privateKey) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec priSpec = new PKCS8EncodedKeySpec(privateKey);
        return ((RSAPrivateKey) keyFactory.generatePrivate(priSpec));
    }

    public static KeyPair generateKey(int bits) throws GeneralSecurityException {
        final KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("RSA");
        pairGenerator.initialize(bits);
        return pairGenerator.generateKeyPair();
    }

    public static String keyToBase64(Key key) {
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(key.getEncoded());
    }

    public static EncryptKey generateEncryptKey(int bits) throws GeneralSecurityException {
        KeyPair keyPair = generateKey(bits);
        String pubKey = keyToBase64(keyPair.getPublic());
        String priKey = keyToBase64(keyPair.getPrivate());
        EncryptKey encryptKey = new EncryptKey();
        encryptKey.setPubKey(pubKey);
        encryptKey.setPriKey(priKey);
        return encryptKey;
    }

    @Data
    public static class EncryptKey {
        private String pubKey;
        private String priKey;
    }
}
