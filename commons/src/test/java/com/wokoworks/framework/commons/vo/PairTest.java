package com.wokoworks.framework.commons.vo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PairTest {

    private final Object[] data;
    private final Object[] expect;

    private Pair pair;

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][]{
            {new Object[]{0, 1}, new Object[]{0, 1}},
            {new Object[]{"0", "1"}, new Object[]{"0", "1"}},
            {new Object[]{null, "1"}, new Object[]{null, "1"}},
            {new Object[]{"0", null}, new Object[]{"0", null}},
            {new Object[]{null, null}, new Object[]{null, null}},
        });
    }

    public PairTest(Object[] data, Object[] expect) {
        this.data = data;
        this.expect = expect;
    }

    @Before
    public void setup() {
        this.pair = Pair.of(data[0], data[1]);
    }

    @Test
    public void of() {
        Assert.assertEquals("first equal", expect[0], this.pair.getFirst());
        Assert.assertEquals("second equal", expect[1], this.pair.getSecond());
    }
}