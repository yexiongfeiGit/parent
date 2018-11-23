package com.wokoworks.framework.commons.tuple;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * java 元组工具类
 * @author 0x0001
 */
public class Tuple {

    /* --------------------- boxed ----------------- */

    public static <A, B> TwoTuple<A, B> of(A first, B second) {
        return new TwoTuple<>(first, second);
    }

    public static <A, B, C> ThreeTuple<A, B, C> of(A first, B second, C three) {
        return new ThreeTuple<>(first, second, three);
    }

    public static <A, B, C, D> FourTuple<A, B, C, D> of(A first, B second, C three, D four) {
        return new FourTuple<>(first, second, three, four);
    }

    /* --------------------- int ----------------- */

    public static IntTuple of(int first, int second) {
        return new IntTuple(first, second);
    }

    public static IntThreeTuple of(int first, int second, int three) {
        return new IntThreeTuple(first, second, three);
    }

    public static IntFourTuple of(int first, int second, int three, int four) {
        return new IntFourTuple(first, second, three, four);
    }

    /* --------------------- long ----------------- */

    public static LongTuple of(long first, long second) {
        return new LongTuple(first, second);
    }

    public static LongThreeTuple of(long first, long second, long three) {
        return new LongThreeTuple(first, second, three);
    }

    public static LongFourTuple of(long first, long second, long three, long four) {
        return new LongFourTuple(first, second, three, four);
    }

    /* --------------------- double ----------------- */

    public static DoubleTuple of(double first, double second) {
        return new DoubleTuple(first, second);
    }

    public static DoubleThreeTuple of(double first, double second, double three) {
        return new DoubleThreeTuple(first, second, three);
    }

    public static DoubleFourTuple of(double first, double second, double three, double four) {
        return new DoubleFourTuple(first, second, three, four);
    }

    /* --------------------- int tuple ----------------- */

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class IntTuple {
        public final int first;
        public final int second;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class IntThreeTuple {
        public final int first;
        public final int second;
        public final int three;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class IntFourTuple {
        public final int first;
        public final int second;
        public final int three;
        public final int four;
    }

    /* --------------------- long tuple ----------------- */

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class LongTuple {
        public final long first;
        public final long second;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class LongThreeTuple {
        public final long first;
        public final long second;
        public final long three;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class LongFourTuple {
        public final long first;
        public final long second;
        public final long three;
        public final long four;
    }

    /* --------------------- double tuple ----------------- */

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class DoubleTuple {
        public final double first;
        public final double second;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class DoubleThreeTuple {
        public final double first;
        public final double second;
        public final double three;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class DoubleFourTuple {
        public final double first;
        public final double second;
        public final double three;
        public final double four;
    }

    /* --------------------- Generic tuple ----------------- */

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class TwoTuple<A, B> {
        public final A first;
        public final B second;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class ThreeTuple<A, B, C> {
        public final A first;
        public final B second;
        public final C three;
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static final class FourTuple<A, B, C, D> {
        public final A first;
        public final B second;
        public final C three;
        public final D four;
    }
}
