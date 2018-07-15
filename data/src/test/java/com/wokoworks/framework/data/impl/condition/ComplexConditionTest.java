package com.wokoworks.framework.data.impl.condition;

import com.wokoworks.framework.data.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ComplexConditionTest {

    @Test
    public void getSql() {
        // or
        {
            final String condition1 = "condition1";
            final String condition2 = "condition2";

            final Condition first = Mockito.mock(Condition.class);
            Mockito.when(first.getSql()).thenReturn(condition1);

            final Condition second = Mockito.mock(Condition.class);
            Mockito.when(second.getSql()).thenReturn(condition2);

            final ComplexCondition condition = ComplexCondition.or(first, second);
            final String sql = condition.getSql();

            Mockito.verify(first).getSql();
            Mockito.verify(second).getSql();

            assertEquals("condition equal", "(" + condition1 + " OR " + condition2 + ")", sql);
        }

        // and
        {
            final String condition1 = "condition1";
            final String condition2 = "condition2";

            final Condition first = Mockito.mock(Condition.class);
            Mockito.when(first.getSql()).thenReturn(condition1);

            final Condition second = Mockito.mock(Condition.class);
            Mockito.when(second.getSql()).thenReturn(condition2);

            final ComplexCondition condition = ComplexCondition.and(first, second);
            final String sql = condition.getSql();

            Mockito.verify(first).getSql();
            Mockito.verify(second).getSql();

            assertEquals("condition equal", "(" + condition1 + " AND " + condition2 + ")", sql);
        }

    }

    @Test
    public void getArgs() {
        // or
        {
            final Object[] args1 = new Object[]{1};
            final Object[] args2 = new Object[]{2};
            final Condition first = Mockito.mock(Condition.class);
            Mockito.when(first.getArgs()).thenReturn(args1);
            Mockito.when(first.getSql()).thenReturn("");

            final Condition second = Mockito.mock(Condition.class);
            Mockito.when(second.getArgs()).thenReturn(args2);
            Mockito.when(second.getSql()).thenReturn("");

            final ComplexCondition condition = ComplexCondition.or(first, second);
            final Object[] args = condition.getArgs();

            Mockito.verify(first).getArgs();
            Mockito.verify(second).getArgs();

            final ArrayList<Object> expectArgs = new ArrayList<>();
            Collections.addAll(expectArgs, args1);
            Collections.addAll(expectArgs, args2);

            assertArrayEquals("args equal", expectArgs.toArray(), args);
        }

        // and
        {
            final Object[] args1 = new Object[]{1};
            final Object[] args2 = new Object[]{2};
            final Condition first = Mockito.mock(Condition.class);
            Mockito.when(first.getArgs()).thenReturn(args1);
            Mockito.when(first.getSql()).thenReturn("");

            final Condition second = Mockito.mock(Condition.class);
            Mockito.when(second.getArgs()).thenReturn(args2);
            Mockito.when(second.getSql()).thenReturn("");

            final ComplexCondition condition = ComplexCondition.and(first, second);
            final Object[] args = condition.getArgs();

            Mockito.verify(first).getArgs();
            Mockito.verify(first).getSql();
            Mockito.verify(second).getArgs();
            Mockito.verify(second).getSql();

            final ArrayList<Object> expectArgs = new ArrayList<>();
            Collections.addAll(expectArgs, args1);
            Collections.addAll(expectArgs, args2);

            assertArrayEquals("args equal", expectArgs.toArray(), args);
        }

    }

    private void testGetSqlNullPoint(boolean firstNull, boolean or) {

        final Condition nullCondition = Mockito.mock(Condition.class);
        Mockito.when(nullCondition.getSql()).thenReturn(null);

        final Condition normalCondition = Mockito.mock(Condition.class);
        Mockito.when(normalCondition.getSql()).thenReturn("");

        ComplexCondition complexCondition;
        if (firstNull) {
            if (or) {
                complexCondition = ComplexCondition.or(nullCondition, normalCondition);
            } else {
                complexCondition = ComplexCondition.and(nullCondition, normalCondition);
            }
        } else {
            if (or) {
                complexCondition = ComplexCondition.or(normalCondition, nullCondition);
            } else {
                complexCondition = ComplexCondition.and(normalCondition, nullCondition);
            }
        }

        try {
            complexCondition.getSql();
            Assert.fail("not throw NullPointerException");
        } catch(NullPointerException e) {
            assertEquals("null point check", NullPointerException.class, e.getClass());
        }

        Mockito.verify(nullCondition).getSql();
        if (!firstNull) {
            Mockito.verify(normalCondition).getSql();
        }
    }

    @Test
    public void getArgsWithException() {

        // first or
        testGetSqlNullPoint(true, true);
        // first and
        testGetSqlNullPoint(true, false);
        // second or
        testGetSqlNullPoint(false, true);
        // second and
        testGetSqlNullPoint(false, false);
    }

}