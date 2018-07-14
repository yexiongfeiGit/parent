package com.wokoworks.framework.commons.utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;


/**
 * @author 0x0001
 */
public final class HashUtils {

    private HashUtils() {
        throw new UnsupportedOperationException("not support");
    }

    /**
     * @deprecated md5 算法不再安全,推荐使用sha256之类算法替代, 密码计算推荐使用 bcrypt之类的方式替换
     */
    @Deprecated
    public static String md5(CharSequence chars) {
        return toHexHash(Hashing.md5(), chars);
    }

    /**
     * @deprecated sha1 算法不再安全,推荐使用sha256之类算法替代, 密码计算推荐使用 bcrypt之类的方式替换
     */
    @Deprecated
    public static String sha1(CharSequence chars) {
        return toHexHash(Hashing.sha1(), chars);
    }

    public static String sha256(CharSequence chars) {
        return toHexHash(Hashing.sha256(), chars);
    }

    public static String toHexHash(HashFunction hashFunction, CharSequence chars) {
        return toHex(hash(hashFunction, chars).asBytes());
    }

    private static HashCode hash(HashFunction hashFunction, CharSequence chars) {
        return hashFunction.hashBytes(chars.toString().getBytes());
    }

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private static String toHex(byte[] bytes) {
        char[] ss = new char[bytes.length * 2];
        int i = 0;
        for (byte aByte : bytes) {
            ss[i++] = HEX_DIGITS[(aByte & 0xF0) >> 4];
            ss[i++] = HEX_DIGITS[aByte & 0x0F];
        }
        return new String(ss);
    }
}
