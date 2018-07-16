package com.wokoworks.framework.commons.encrypt;

import com.wokoworks.framework.commons.utils.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RSAEncryptionTest {

    private RSAEncryption rsaEncryption;

    @Before
    public void setup() throws Exception {
        final KeyPair keyPair = RSAEncryption.generateKey(1024);
        rsaEncryption = new RSAEncryption(((RSAPublicKey) keyPair.getPublic()), ((RSAPrivateKey) keyPair.getPrivate()));
    }

    @Test
    public void encrypt() {
        final byte[] data = RandomUtils.randomBytes(102400);
        final byte[] encryptData = rsaEncryption.encrypt(data);
        final byte[] decryptData = rsaEncryption.decrypt(encryptData);

        assertArrayEquals("encrypt -> decrypt equal", data, decryptData);

    }

    @Test
    public void generateKey() throws Exception {
        for (int size : new int[]{1024, 2048}) {
            final KeyPair keyPair = RSAEncryption.generateKey(size);
            assertNotNull("key pair", keyPair);
        }
    }

    @Test
    public void generateEncryptKey() throws Exception {
        final RSAEncryption.EncryptKey encryptKey = RSAEncryption.generateEncryptKey(1024);
        assertNotNull("public key", encryptKey.getPubKey());
        assertNotNull("private key", encryptKey.getPriKey());

        final Base64.Decoder decode = Base64.getDecoder();
        final byte[] pubKey = decode.decode(encryptKey.getPubKey());
        final byte[] priKey = decode.decode(encryptKey.getPriKey());

        assertNotNull("public key to byte array", pubKey);
        assertNotNull("private key to byte array", priKey);

        final RSAPublicKey rsaPublicKey = RSAEncryption.generatePublicKey(pubKey);
        final RSAPrivateKey rsaPrivateKey = RSAEncryption.generatePrivateKey(priKey);
        assertNotNull("rsa public key", rsaPublicKey);
        assertNotNull("rsa private key", rsaPrivateKey);

        final String base64PubKey = RSAEncryption.keyToBase64(rsaPublicKey);
        final String base64PriKey = RSAEncryption.keyToBase64(rsaPrivateKey);

        assertEquals("base64 public key equal", encryptKey.getPubKey(), base64PubKey);
        assertEquals("base64 private key equal", encryptKey.getPriKey(), base64PriKey);


    }
}