package com.wokoworks.framework.commons.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Implements AES (Advanced Encryption Standard) with Galois/Counter Mode (GCM), which is a mode of
 * operation for symmetric key cryptographic block ciphers that has been widely adopted because of
 * its efficiency and performance.
 * <p>
 * Every encryption produces a new 12 byte random IV (see http://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38d.pdf)
 * because the security of GCM depends choosing a unique initialization vector for every encryption performed with the same key.
 * <p>
 * The iv, encrypted content and auth tag will be encoded to the following format:
 * <p>
 * out = byte[] {x y y y y y y y y y y y y z z z ...}
 * <p>
 * x = IV length as byte
 * y = IV bytes
 * z = content bytes
 *
 * @author Patrick Favre-Bulle
 * @since 18.12.2017
 */
@SuppressWarnings("WeakerAccess")
public final class AesGcmEncryption implements AuthenticatedEncryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    private final SecureRandom secureRandom;
    private final Provider provider;
    private Cipher mCipher;

    public AesGcmEncryption() {
        this(new SecureRandom(), null);
    }

    public AesGcmEncryption(SecureRandom secureRandom) {
        this(secureRandom, null);
    }

    public AesGcmEncryption(SecureRandom secureRandom, Provider provider) {
        this.secureRandom = secureRandom;
        this.provider = provider;
    }

    @Override
    public byte[] encrypt(byte[] rawEncryptionKey, byte[] rawData, byte[] associatedData) throws AuthenticatedEncryptionException {
        if (rawEncryptionKey.length < 16) {
            throw new IllegalArgumentException("key length must be longer than 16 byte");
        }

        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            secureRandom.nextBytes(iv);

            final Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(rawEncryptionKey, "AES"), new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }

            byte[] encrypted = cipher.doFinal(rawData);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + encrypted.length);
            byteBuffer.put((byte) iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);
            return byteBuffer.array();
        } catch (Exception e) {
            throw new AuthenticatedEncryptionException("could not encrypt", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] rawEncryptionKey, byte[] encryptedData, byte[] associatedData) throws AuthenticatedEncryptionException {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);

            int ivLength = byteBuffer.get();
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);
            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);

            final Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(rawEncryptionKey, "AES"), new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }
            byte[] decrypted = cipher.doFinal(encrypted);

            final byte secureByte = (byte) 0;
            Arrays.fill(iv, secureByte);
            Arrays.fill(rawEncryptionKey, secureByte);
            Arrays.fill(encrypted, secureByte);

            return decrypted;
        } catch (Exception e) {
            throw new AuthenticatedEncryptionException("could not decrypt", e);
        }
    }

    @Override
    public int byteSizeLength(int keyStrengthType) {
        return keyStrengthType == STRENGTH_HIGH ? 16 : 32;
    }

    private Cipher getCipher() {
        if (mCipher == null) {
            try {
                if (provider != null) {
                    mCipher = Cipher.getInstance(ALGORITHM, provider);
                } else {
                    mCipher = Cipher.getInstance(ALGORITHM);
                }
            } catch (Exception e) {
                throw new IllegalStateException("could not get cipher instance", e);
            }
        }
        return mCipher;
    }
}