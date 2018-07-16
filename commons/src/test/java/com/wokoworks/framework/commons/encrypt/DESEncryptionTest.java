package com.wokoworks.framework.commons.encrypt;

import com.wokoworks.framework.commons.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class DESEncryptionTest {

    @Test
    public void encrypt() throws Exception {
        final DESEncryption desEncryption = new DESEncryption();
        byte[] password = RandomUtils.randomBytes(128);
        byte[] data = "Hello World".getBytes();

        final byte[] encryptData = desEncryption.encrypt(password, data);
        Assert.assertNotNull("encrypt data not null", encryptData);

        final byte[] decryptData = desEncryption.decrypt(password, encryptData);
        assertNotNull("decrypt data not null", decryptData);

        assertArrayEquals("encrypt -> decrypt equal", data, decryptData);

    }

    @Test
    public void encrypt1() throws Exception {
        final DESEncryption desEncryption = new DESEncryption();
        byte[] password = RandomUtils.randomBytes(128);
        byte[] data = "Hello World".getBytes();

        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        desEncryption.encrypt(password, in, out);
        final byte[] encryptData = out.toByteArray();
        Assert.assertNotNull("encrypt data not null", encryptData);


        final ByteArrayInputStream decryptIn = new ByteArrayInputStream(encryptData);
        final ByteArrayOutputStream decryptOut = new ByteArrayOutputStream();
        desEncryption.decrypt(password, decryptIn, decryptOut);
        final byte[] decryptData = decryptOut.toByteArray();
        assertNotNull("decrypt data not null", decryptData);

        assertArrayEquals("encrypt -> decrypt equal", data, decryptData);
    }
}