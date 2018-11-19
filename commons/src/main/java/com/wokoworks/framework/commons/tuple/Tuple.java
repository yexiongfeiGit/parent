package com.wokoworks.framework.commons.tuple;

/**
 * java 元组工具类
 * @author 0x0001
 */
public class Tuple {

    public static <A, B> TwoTuple<A, B> of(A first, B second) {
        return new TwoTuple<>(first, second);
    }

    public static <A, B, C> ThreeTuple<A, B, C> of(A first, B second, C three) {
        return new ThreeTuple<>(first, second, three);
    }

    public static <A, B, C, D> FourTuple<A, B, C, D> of(A first, B second, C three, D four) {
        return new FourTuple<>(first, second, three, four);
    }

    public static class TwoTuple<A, B> {

        public final A first;
        public final B second;

        TwoTuple(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class ThreeTuple<A, B, C> extends TwoTuple<A, B> {
        public final C three;

        ThreeTuple(A first, B second, C three) {
            super(first, second);
            this.three = three;
        }
    }

    public static class FourTuple<A, B, C, D> extends ThreeTuple<A, B, C> {
        public final D four;

        FourTuple(A first, B second, C three, D four) {
            super(first, second, three);
            this.four = four;
        }
    }
}
