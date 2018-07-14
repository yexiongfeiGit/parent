package com.wokoworks.framework.commons.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 0x0001
 */
public final class RandomUtils {

    private RandomUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 数字
     */
    public static final String RANDOM_DIGITAL = "0123456789";
    /**
     * 字母
     */
    public static final String RANDOM_LETTER = "abcdefghighkilmnopqrstuvwxyz";
    /**
     * 字母加数字
     */
    public static final String RANDOM_LETTER_DIGITAL = RANDOM_LETTER + RANDOM_DIGITAL;


    public static String randomNumberWithFixedLength(int length) {
        return randomWithCharacters(RANDOM_DIGITAL.toCharArray(), length);
    }

    public static byte[] randomBytes(int size) {
        final SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[size];
        secureRandom.nextBytes(bytes);
        return bytes;
    }


    public static String randomWithCharacters(char[] seed, int length) {
        final Random random = new Random();
        return Stream.generate(() -> seed[random.nextInt(seed.length)])
            .limit(length)
            .map(String::valueOf)
            .collect(Collectors.joining(""));
    }

}
