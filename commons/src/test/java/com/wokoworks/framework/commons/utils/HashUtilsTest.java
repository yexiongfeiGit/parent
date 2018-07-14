package com.wokoworks.framework.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HashUtilsTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testConstruct() throws Throwable {
        final Constructor<HashUtils> constructor = HashUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch(InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void md5() {
        Map<String, String> result = new HashMap<>();
        result.put("111111", "96e79218965eb72c92a549dd5a330112");
        result.put("222222", "e3ceb5881a0a1fdaad01296d7554868d");
        result.put("333333", "1a100d2c0dab19c4430e7d73762b3423");
        result.put("444444", "73882ab1fa529d7273da0db6b49cc4f3");

        for (Map.Entry<String, String> entry : result.entrySet()) {
            final String data = entry.getKey();
            final String expect = entry.getValue();
            final String actual = HashUtils.md5(data);

            Assert.assertEquals("md5 result equal", expect, actual);
        }
    }

    @Test
    public void sha1() {
        Map<String, String> result = new HashMap<>();
        result.put("111111", "3d4f2bf07dc1be38b20cd6e46949a1071f9d0e3d");
        result.put("222222", "273a0c7bd3c679ba9a6f5d99078e36e85d02b952");
        result.put("333333", "77bce9fb18f977ea576bbcd143b2b521073f0cd6");
        result.put("444444", "42cfe854913594fe572cb9712a188e829830291f");

        for (Map.Entry<String, String> entry : result.entrySet()) {
            final String data = entry.getKey();
            final String expect = entry.getValue();
            final String actual = HashUtils.sha1(data);

            Assert.assertEquals("sha1 result equal", expect, actual);
        }
    }

    @Test
    public void sha256() {
        Map<String, String> result = new HashMap<>();
        result.put("111111", "bcb15f821479b4d5772bd0ca866c00ad5f926e3580720659cc80d39c9d09802a");
        result.put("222222", "4cc8f4d609b717356701c57a03e737e5ac8fe885da8c7163d3de47e01849c635");
        result.put("333333", "68487dc295052aa79c530e283ce698b8c6bb1b42ff0944252e1910dbecdc5425");
        result.put("444444", "69f7f7a7f8bca9970fa6f9c0b8dad06901d3ef23fd599d3213aa5eee5621c3e3");

        for (Map.Entry<String, String> entry : result.entrySet()) {
            final String data = entry.getKey();
            final String expect = entry.getValue();
            final String actual = HashUtils.sha256(data);

            Assert.assertEquals("sha1 result equal", expect, actual);
        }
    }
}