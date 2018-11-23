package com.wokoworks.framework.commons.tuple;

import org.junit.Assert;
import org.junit.Test;

public class TupleTest {

    @Test
    public void of() {
        final String first = "first";
        final String second = "second";
        final Tuple.TwoTuple<String, String> tuple = Tuple.of(first, second);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
    }

    @Test
    public void of1() {
        final String first = "first";
        final String second = "second";
        final String three = "three";
        final Tuple.ThreeTuple<String, String, String> tuple = Tuple.of(first, second, three);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
        Assert.assertEquals("three equal", three, tuple.three);
    }

    @Test
    public void of2() {
        final String first = "first";
        final String second = "second";
        final String three = "three";
        final String four = "four";
        final Tuple.FourTuple<String, String, String, String> tuple = Tuple.of(first, second, three, four);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
        Assert.assertEquals("three equal", three, tuple.three);
        Assert.assertEquals("four equal", four, tuple.four);
    }

    /* int test */

    @Test
    public void ofInt() {
        final int first = 1;
        final int second = 2;
        final Tuple.IntTuple tuple = Tuple.of(first, second);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
    }

    @Test
    public void ofInt1() {
        final int first = 1;
        final int second = 2;
        final int three = 3;
        final Tuple.IntThreeTuple tuple = Tuple.of(first, second, three);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
        Assert.assertEquals("three equal", three, tuple.three);
    }

    @Test
    public void ofInt2() {
        final int first = 1;
        final int second = 2;
        final int three = 3;
        final int four = 4;
        final Tuple.IntFourTuple tuple = Tuple.of(first, second, three, four);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
        Assert.assertEquals("three equal", three, tuple.three);
        Assert.assertEquals("four equal", four, tuple.four);
    }

    /* long test */

    @Test
    public void ofLong() {
        final long first = 1;
        final long second = 2;
        final Tuple.LongTuple tuple = Tuple.of(first, second);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
    }

    @Test
    public void ofLong1() {
        final long first = 1;
        final long second = 2;
        final long three = 3;
        final Tuple.LongThreeTuple tuple = Tuple.of(first, second, three);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
        Assert.assertEquals("three equal", three, tuple.three);
    }

    @Test
    public void ofLong2() {
        final long first = 1;
        final long second = 2;
        final long three = 3;
        final long four = 4;
        final Tuple.LongFourTuple tuple = Tuple.of(first, second, three, four);

        Assert.assertEquals("first equal", first, tuple.first);
        Assert.assertEquals("second equal", second, tuple.second);
        Assert.assertEquals("three equal", three, tuple.three);
        Assert.assertEquals("four equal", four, tuple.four);
    }

    /* double test */
    @Test
    public void ofDouble() {
        final double first = 1.01;
        final double second = 2.01;
        final Tuple.DoubleTuple tuple = Tuple.of(first, second);

        Assert.assertEquals("first equal", first, tuple.first, 0.01);
        Assert.assertEquals("second equal", second, tuple.second, 0.01);
    }

    @Test
    public void ofDouble1() {
        final double first = 1.01;
        final double second = 2.01;
        final double three = 3.01;
        final Tuple.DoubleThreeTuple tuple = Tuple.of(first, second, three);

        Assert.assertEquals("first equal", first, tuple.first, 0.01);
        Assert.assertEquals("second equal", second, tuple.second, 0.01);
        Assert.assertEquals("three equal", three, tuple.three, 0.01);
    }

    @Test
    public void ofDouble2() {
        final double first = 1.01;
        final double second = 2.01;
        final double three = 3.01;
        final double four = 4.01;
        final Tuple.DoubleFourTuple tuple = Tuple.of(first, second, three, four);

        Assert.assertEquals("first equal", first, tuple.first, 0.01);
        Assert.assertEquals("second equal", second, tuple.second, 0.01);
        Assert.assertEquals("three equal", three, tuple.three, 0.01);
        Assert.assertEquals("four equal", four, tuple.four, 0.01);
    }
}