package com.wokoworks.framework.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RandomUtilsTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testConstruct() throws Throwable {
        final Constructor<RandomUtils> constructor = RandomUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch(InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void randomNumberWithFixedLength() {
        for (int i = 0; i < 100; i++) {
            final String s = RandomUtils.randomNumberWithFixedLength(i);
            Assert.assertEquals("length equal", i, s.length());
            for (char ch :s.toCharArray()){
                Assert.assertTrue("index of", "0123456789".indexOf(ch) >= 0);
            }
        }
    }

    @Test
    public void randomBytes() {
        for (int i = 0; i < 100; i++) {
            final byte[] bytes = RandomUtils.randomBytes(i);
            Assert.assertEquals("bytes length", i, bytes.length);

            byte[] b = new byte[i];
            Assert.assertNotEquals("", b, bytes);
        }
    }

    @Test
    public void randomWithCharacters() {
        for (int i = 0; i < 100; i++) {
            final String s = RandomUtils.randomWithCharacters(RandomUtils.RANDOM_LETTER_DIGITAL.toCharArray(), i);
            Assert.assertEquals("length equal", i, s.length());
            for (char ch :s.toCharArray()){
                Assert.assertTrue("index of", RandomUtils.RANDOM_LETTER_DIGITAL.indexOf(ch) >= 0);
            }
        }
    }
}