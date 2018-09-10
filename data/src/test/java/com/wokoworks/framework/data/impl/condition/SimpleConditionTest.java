package com.wokoworks.framework.data.impl.condition;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SimpleConditionTest {

    @Test
    public void getSql() {
        final String field = "field";
        final Object[] arg = {1};
        for (Opt opt : Opt.values()) {
            final SimpleCondition condition = new SimpleCondition(field, opt, arg);
            final String sql = condition.getSql();

            String expectSql = String.format("%s %s ", field, opt.symbol);

            if (opt == Opt.IN || opt == Opt.NOT_IN) {
                expectSql = expectSql + "(?)";
            } else if (opt == Opt.LIKE || opt == Opt.NOT_LIKE) {
                expectSql = expectSql + "'%" + arg + "%'";
            } else {
                expectSql = expectSql + "?";
            }
            assertEquals("sql equal", expectSql, sql);
        }

    }

    @Test
    public void getArgs() {
        final String field = "field";
        final Object[] arg = {1};
        for (Opt opt : Opt.values()) {
            final SimpleCondition condition = new SimpleCondition(field, opt, arg);
            final Object[] args = condition.getArgs();
            if (opt == Opt.IN || opt == Opt.NOT_IN) {
                assertArrayEquals("args equal", arg, args);
            } else if (opt == Opt.LIKE || opt == Opt.NOT_LIKE) {
                assertArrayEquals("args equal", new Object[]{}, args);
            } else {
                assertArrayEquals("args equal", new Object[]{arg}, args);
            }
        }
    }
}