package com.wokoworks.framework.commons.utils;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author 0x0001
 */
public final class RandomUtils {

    private RandomUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * number
     */
    public static final String RANDOM_DIGITAL = "0123456789";
    /**
     * letter
     */
    public static final String RANDOM_LETTER = "abcdefghighkilmnopqrstuvwxyz";
    /**
     * Letter plus numbers
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
        return new Random().ints(length, 0, seed.length)
            .mapToObj(i -> String.valueOf(seed[i]))
            .collect(Collectors.joining());
    }

}
