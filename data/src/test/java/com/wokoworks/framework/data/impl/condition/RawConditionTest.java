package com.wokoworks.framework.data.impl.condition;

import com.wokoworks.framework.data.Condition;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RawConditionTest {

    private Condition getRawCondition(String sql, Object[] args) {
        return new RawCondition(sql, args);
    }

    @Test
    public void test() {
        final String sql = "condition";
        final Object[] args = {1};
        final Condition condition = getRawCondition(sql, args);

        assertEquals("sql equals", sql, condition.getSql());
        assertArrayEquals("args equal", args, condition.getArgs());
        System.out.println(condition);
    }

    @Test
    public void equals() {
        final String sql = "condition";
        final Object[] args = {1};
        final Condition condition1 = getRawCondition(sql, args);
        final Condition condition2 = getRawCondition(sql, args);

        assertEquals("equal test", condition1, condition2);

        {
            final Condition condition3 = getRawCondition(sql, new Object[]{1});
            assertEquals("equals array", condition1, condition3);
        }
    }

    @Test
    public void testHashCode() {
        final String sql = "condition";
        final Object[] args = {1};
        {
            final Condition condition1 = getRawCondition(sql, args);
            final Condition condition2 = getRawCondition(sql, args);

            assertEquals("hash code equal", condition1.hashCode(), condition2.hashCode());

        }
        {
            final Condition condition1 = getRawCondition(sql, args);
            final Condition condition2 = getRawCondition("sql2", args);
            assertNotEquals("not equal", condition1.hashCode(), condition2.hashCode());
        }
    }
}