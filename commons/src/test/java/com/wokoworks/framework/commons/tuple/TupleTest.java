package com.wokoworks.framework.commons.tuple;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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
}