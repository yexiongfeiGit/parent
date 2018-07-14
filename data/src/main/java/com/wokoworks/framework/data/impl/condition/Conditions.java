package com.wokoworks.framework.data.impl.condition;


import com.google.common.base.Preconditions;
import com.wokoworks.framework.data.Condition;

import java.util.Collection;

/**
 * @author 0x0001
 */
public final class Conditions {
    private Conditions() {
        throw new UnsupportedOperationException();
    }

    public static Condition equals(String field, Object arg) {
        return new SimpleCondition(field, Opt.EQ, arg);
    }

    public static Condition lessThen(String field, Object arg) {
        return new SimpleCondition(field, Opt.LT, arg);
    }

    public static Condition greaterThen(String field, Object arg) {
        return new SimpleCondition(field, Opt.GT, arg);
    }

    public static Condition lessThanEquals(String field, Object arg) {
        return new SimpleCondition(field, Opt.LTE, arg);
    }

    public static Condition greaterThanEquals(String field, Object arg) {
        return new SimpleCondition(field, Opt.GTE, arg);
    }

    public static Condition in(String field, Object[] args) {
        return new SimpleCondition(field, Opt.IN, args);
    }

    public static Condition in(String field, Collection<?> args) {
        return in(field, args.toArray());
    }

    public static Condition notIn(String field, Object[] args) {
        return new SimpleCondition(field, Opt.NOT_IN, args);
    }

    public static Condition notIn(String field, Collection<?> args) {
        return notIn(field, args.toArray());
    }

    public static Condition or(Condition... conditions) {
        Preconditions.checkNotNull(conditions);
        Preconditions.checkArgument(conditions.length > 0);

        Condition condition = conditions[0];
        for (int i = 1; i < conditions.length; i++) {
            condition = ComplexCondition.or(condition, conditions[i]);
        }
        return condition;
    }

    public static Condition and(Condition... conditions) {
        Preconditions.checkNotNull(conditions);
        Preconditions.checkArgument(conditions.length > 0);

        Condition condition = conditions[0];
        for (int i = 1; i < conditions.length; i++) {
            condition = ComplexCondition.and(condition, conditions[i]);
        }
        return condition;
    }
}
