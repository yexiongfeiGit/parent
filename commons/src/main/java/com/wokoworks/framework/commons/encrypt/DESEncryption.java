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
 * DES It's not a safe encryption method now，Mainly because it uses 56 The key is too short。1999 Year 1 moon，distributed.net Cooperate with the Electronic Outpost Foundation，exist 22 Hour 15 In the minute, a public cracked one publicly DES Key。There are also some analysis reports that the theoretical weakness of the algorithm proposes，Although it is difficult to apply in practice。To provide practical safety required，Be available DES Derived algorithm 3DES Called，Although 3DES There are also theoretical attack methods。exist 2001 Year，DES As a standard, it has been high -level encryption standard（AES）Replace。in addition，DES No longer as the National Standard Technology Association（Former Standard Agency）A standard。
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
        //Create a key factory，Then use it to put it DESKeySpec Convert
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretkey = keyFactory.generateSecret(desKey);
        //Cipher The object actually completes the encryption operation
        Cipher cipher = Cipher.getInstance("DES");
        if (mode == Mode.ENCRYPT) {
            cipher.init(Cipher.ENCRYPT_MODE, secretkey, random);
        } else if (mode == Mode.DECRYPT) {
            cipher.init(Cipher.DECRYPT_MODE, secretkey, random);
        }

        return cipher;
    }
}
