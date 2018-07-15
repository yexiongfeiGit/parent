package com.wokoworks.framework.data.impl.condition;

enum Opt {
    EQ("="), LT("<"), LTE("<="), GT(">"), GTE(">="), IN("IN"), NOT_IN("NOT IN"), LIKE("LIKE"), NOT_LIKE("NOT LIKE");

    Opt(String symbol) {
        this.symbol = symbol;
    }


    public final String symbol;

}