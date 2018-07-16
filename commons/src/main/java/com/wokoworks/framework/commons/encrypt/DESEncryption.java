package com.wokoworks.framework.commons.encrypt;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * https://zh.wikipedia.org/wiki/%E8%B3%87%E6%96%99%E5%8A%A0%E5%AF%86%E6%A8%99%E6%BA%96
 * DES现在已经不是一种安全的加密方法，主要因为它使用的56位密钥过短。1999年1月，distributed.net与电子前哨基金会合作，在22小时15分钟内即公开破解了一个DES密钥。也有一些分析报告提出了该算法的理论上的弱点，虽然在实际中难以应用。为了提供实用所需的安全性，可以使用DES的派生算法3DES来进行加密，虽然3DES也存在理论上的攻击方法。在2001年，DES作为一个标准已经被高级加密标准（AES）所取代。另外，DES已经不再作为国家标准科技协会（前国家标准局）的一个标准。
 *
 * @author 0x0001
 */
public class DESEncryption {

    private enum Mode {
        ENCRYPT, DECRYPT,
    }

    public byte[] encrypt(byte[] password, byte[] data) throws GeneralSecurityException {
        return doFinal(password, data, Mode.ENCRYPT);
    }

    public byte[] decrypt(byte[] password, byte[] data) throws GeneralSecurityException {
        return doFinal(password, data, Mode.DECRYPT);
    }

    public void encrypt(byte[] password, InputStream in, OutputStream out) throws GeneralSecurityException, IOException {
        doFinal(password, in, out, Mode.ENCRYPT);
    }

    public void decrypt(byte[] password, InputStream in, OutputStream out) throws GeneralSecurityException, IOException {
        doFinal(password, in, out, Mode.DECRYPT);
    }

    private void doFinal(byte[] password, InputStream in, OutputStream out, Mode mode) throws GeneralSecurityException, IOException {
        final Cipher cipher = getCipher(password, mode);
        byte[] bytes = new byte[512];
        try (BufferedInputStream bin = new BufferedInputStream(in, bytes.length * 2)) {
            while (true) {
                final int len = bin.read(bytes);
                if (len < 0) {
                    break;
                }
                final byte[] encryptData = cipher.update(bytes, 0, len);
                out.write(encryptData);
            }
        }
        final byte[] finalBytes = cipher.doFinal();
        out.write(finalBytes);
    }

    private byte[] doFinal(byte[] password, byte[] data, Mode mode) throws GeneralSecurityException {
        Cipher cipher = getCipher(password, mode);
        return cipher.doFinal(data);
    }

    private Cipher getCipher(byte[] password, Mode mode) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(password);
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretkey = keyFactory.generateSecret(desKey);
        //Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");
        if (mode == Mode.ENCRYPT) {
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, random);
        } else if (mode == Mode.DECRYPT) {
            cipher.init(Cipher.DECRYPT_MODE, secretkey, random);
        }

        return cipher;
    }
}
