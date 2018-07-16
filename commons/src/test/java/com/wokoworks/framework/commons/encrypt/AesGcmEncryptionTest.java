package com.wokoworks.framework.commons.encrypt;

import com.wokoworks.framework.commons.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

/**
 * aes gcm 加解密测试, oracle jvm 需要下载jar替换才能正常运行, https://backstage.forgerock.com/knowledge/kb/article/a17171105
 * java8 http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * java7 http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
 * java6 http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
 * <p>
 * 下载zip文件,替换 $JAVA_HOME/jre/lib/security/里的 `local_policy.jar`, `US_export_policy.jar`
 */
public class AesGcmEncryptionTest {

    @Test(expected = IllegalArgumentException.class)
    public void encryptException() throws Exception {
        final AesGcmEncryption encryption = new AesGcmEncryption();
        final byte[] rawEncryptionKey = new byte[15];
        encryption.encrypt(rawEncryptionKey, "abc".getBytes(), null);
    }


    @Test
    public void encrypt() throws Exception {

        testOk(new AesGcmEncryption(new SecureRandom()), null);
        testOk(new AesGcmEncryption(), null);


        byte[] associatedData = RandomUtils.randomBytes(128);
        testOk(new AesGcmEncryption(new SecureRandom()), associatedData);
        testOk(new AesGcmEncryption(), associatedData);


    }

    private void testOk(AesGcmEncryption aesGcmEncryption, byte[] associatedData) throws AuthenticatedEncryptionException {
        String encData = "Hello world!";

        // 16 (AES-128)
        // 24 (AES-192)
        // 32 (AES-256)

        int[] keyLength = new int[]{128, 192, 256};

        // 128 bit 对应byte长度
        final int i = aesGcmEncryption.byteSizeLength(AuthenticatedEncryption.STRENGTH_HIGH);
        assertEquals(16, i);
        // 256 bit 对应byte长度
        final int i1 = aesGcmEncryption.byteSizeLength(AuthenticatedEncryption.STRENGTH_VERY_HIGH);
        assertEquals(32, i1);

        for (int length : keyLength) {
            byte[] password = new byte[length / 8];
            final byte[] encryptedData = aesGcmEncryption.encrypt(password, encData.getBytes(), associatedData);

            Assert.assertNotEquals(new String(encryptedData), encData);

            final byte[] decryptedData = aesGcmEncryption.decrypt(password, encryptedData, associatedData);
            assertEquals(new String(decryptedData), encData);
        }
    }

}