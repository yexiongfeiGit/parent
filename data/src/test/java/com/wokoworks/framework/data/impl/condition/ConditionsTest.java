package com.wokoworks.framework.data.impl.condition;

import com.wokoworks.framework.data.Condition;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ConditionsTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testConstruct() throws Throwable {
        final Constructor<Conditions> constructor = Conditions.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Test
    public void equals() {
        final Condition condition = Conditions.equals("name", "value");
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name = ?", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);

    }

    @Test
    public void lessThen() {
        final Condition condition = Conditions.lessThen("name", "value");
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name < ?", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void greaterThen() {
        final Condition condition = Conditions.greaterThen("name", "value");
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name > ?", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void lessThanEquals() {
        final Condition condition = Conditions.lessThanEquals("name", "value");
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name <= ?", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void greaterThanEquals() {
        final Condition condition = Conditions.greaterThanEquals("name", "value");
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name >= ?", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void like() {
        final String value = "value";
        final Condition condition = Conditions.like("name", value);
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name LIKE ?", sql);
        assertArrayEquals("arg equal", new Object[]{value}, args);
    }

    @Test
    public void notLike() {
        final String value = "value";
        final Condition condition = Conditions.notLike("name", value);
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name NOT LIKE ?", sql);
        assertArrayEquals("arg equal", new Object[]{value}, args);
    }

    @Test
    public void in() {
        final Condition condition = Conditions.in("name", new Object[]{"value"});
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name IN (?)", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void in1() {
        final Condition condition = Conditions.in("name", Collections.singletonList("value"));
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name IN (?)", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void notIn() {
        final Condition condition = Conditions.notIn("name", new Object[]{"value"});
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name NOT IN (?)", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test
    public void notIn1() {
        final Condition condition = Conditions.notIn("name", Collections.singletonList("value"));
        final String sql = condition.getSql();
        final Object[] args = condition.getArgs();

        assertEquals("sql equal", "name NOT IN (?)", sql);
        assertArrayEquals("arg equal", new Object[]{"value"}, args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void or() {
        Conditions.or();
        Assert.fail("");
    }

    @Test(expected = NullPointerException.class)
    public void or1() {
        Conditions.or((Condition[]) null);
        Assert.fail("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void and() {
        Conditions.and();
    }

    @Test(expected = NullPointerException.class)
    public void and1() {
        Conditions.and((Condition[]) null);
    }
}