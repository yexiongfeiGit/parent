package com.wokoworks.framework.data.impl.condition;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RawConditionTest {

    @Test
    public void test() {
        final String sql = "condition";
        final Object[] args = {1};
        final RawCondition condition = new RawCondition(sql, args);

        assertEquals("sql equals", sql, condition.getSql());
        assertArrayEquals("args equal", args, condition.getArgs());
        System.out.println(condition);
    }

    @Test
    public void equals() {
        final String sql = "condition";
        final Object[] args = {1};
        final RawCondition condition1 = new RawCondition(sql, args);
        final RawCondition condition2 = new RawCondition(sql, args);

        assertEquals("equal test", condition1, condition2);

        {
            final RawCondition condition3 = new RawCondition(sql, new Object[]{1});
            assertEquals("equals array", condition1, condition3);
        }
    }

    @Test
    public void testHashCode() {
        final String sql = "condition";
        final Object[] args = {1};
        {
            final RawCondition condition1 = new RawCondition(sql, args);
            final RawCondition condition2 = new RawCondition(sql, args);

            assertEquals("hash code equal", condition1.hashCode(), condition2.hashCode());

        }
        {
            final RawCondition condition1 = new RawCondition(sql, args);
            final RawCondition condition2 = new RawCondition("sql2", args);
            assertNotEquals("not equal", condition1.hashCode(), condition2.hashCode());
        }
    }
}