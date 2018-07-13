package com.wokoworks.framework.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ByteUnitTest {

    @Test
    public void toBytes() {
        Map<ByteUnit, Double> testMap = new HashMap<>();
        testMap.put(ByteUnit.BYTES, (double) 1L);
        testMap.put(ByteUnit.KILOBYTES, (double) (1L << 10));
        testMap.put(ByteUnit.MEGABYTES, (double) (1L << 20));
        testMap.put(ByteUnit.GIGABYTES, (double) (1L << 30));
        testMap.put(ByteUnit.TERABYTES, (double) (1L << 40));
        testMap.put(ByteUnit.PETABYTES, (double) (1L << 50));

        check(testMap, u -> u.toBytes(1));
    }

    @Test
    public void toKilobytes() {
        Map<ByteUnit, Double> testMap = new HashMap<>();
        testMap.put(ByteUnit.BYTES, (double) 1L / Math.pow(1 << 10, 1));
        testMap.put(ByteUnit.KILOBYTES, (double) 1L);
        testMap.put(ByteUnit.MEGABYTES, (double) (1L << 10));
        testMap.put(ByteUnit.GIGABYTES, (double) (1L << 20));
        testMap.put(ByteUnit.TERABYTES, (double) (1L << 30));
        testMap.put(ByteUnit.PETABYTES, (double) (1L << 40));

        check(testMap, u -> u.toKilobytes(1));
    }

    @Test
    public void toMegabytes() {
        Map<ByteUnit, Double> testMap = new HashMap<>();
        testMap.put(ByteUnit.BYTES, (double) 1L / Math.pow(1 << 10, 2));
        testMap.put(ByteUnit.KILOBYTES, (double) 1L / Math.pow(1 << 10, 1));
        testMap.put(ByteUnit.MEGABYTES, (double) 1L);
        testMap.put(ByteUnit.GIGABYTES, (double) (1L << 10));
        testMap.put(ByteUnit.TERABYTES, (double) (1L << 20));
        testMap.put(ByteUnit.PETABYTES, (double) (1L << 30));

        check(testMap, u -> u.toMegabytes(1));
    }

    @Test
    public void toGigabytes() {
        Map<ByteUnit, Double> testMap = new HashMap<>();
        testMap.put(ByteUnit.BYTES, (double) 1L / Math.pow(1 << 10, 3));
        testMap.put(ByteUnit.KILOBYTES, (double) 1L / Math.pow(1 << 10, 2));
        testMap.put(ByteUnit.MEGABYTES, (double) 1L / Math.pow(1 << 10, 1));
        testMap.put(ByteUnit.GIGABYTES, (double) 1L);
        testMap.put(ByteUnit.TERABYTES, (double) (1L << 10));
        testMap.put(ByteUnit.PETABYTES, (double) (1L << 20));

        check(testMap, u -> u.toGigabytes(1));
    }

    @Test
    public void toTerabytes() {
        Map<ByteUnit, Double> testMap = new HashMap<>();
        testMap.put(ByteUnit.BYTES, (double) 1L / Math.pow(1 << 10, 4));
        testMap.put(ByteUnit.KILOBYTES, (double) 1L / Math.pow(1 << 10, 3));
        testMap.put(ByteUnit.MEGABYTES, (double) 1L / Math.pow(1 << 10, 2));
        testMap.put(ByteUnit.GIGABYTES, (double) 1L / Math.pow(1 << 10, 1));
        testMap.put(ByteUnit.TERABYTES, (double) 1L);
        testMap.put(ByteUnit.PETABYTES, (double) (1L << 10));

        check(testMap, u -> u.toTerabytes(1));
    }

    @Test
    public void toPetabytes() {
        Map<ByteUnit, Double> testMap = new HashMap<>();
        testMap.put(ByteUnit.BYTES, (double) 1L / Math.pow(1 << 10, 5));
        testMap.put(ByteUnit.KILOBYTES, (double) 1L / Math.pow(1 << 10, 4));
        testMap.put(ByteUnit.MEGABYTES, (double) 1L / Math.pow(1 << 10, 3));
        testMap.put(ByteUnit.GIGABYTES, (double) 1L / Math.pow(1 << 10, 2));
        testMap.put(ByteUnit.TERABYTES, (double) 1L /Math.pow(1 << 10, 1));
        testMap.put(ByteUnit.PETABYTES, (double) 1L);

        check(testMap, u -> u.toPetabytes(1));
    }

    private void check(Map<ByteUnit, Double> data, Function<ByteUnit, Double> test) {
        for (Map.Entry<ByteUnit, Double> entry : data.entrySet()) {
            final ByteUnit unit = entry.getKey();
            final Double size = entry.getValue();

            Assert.assertEquals(unit + " size equal", size, test.apply(unit));
        }
    }
}