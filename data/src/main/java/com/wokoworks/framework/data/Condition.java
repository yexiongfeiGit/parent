package com.wokoworks.framework.data;

/**
 * @author 0x0001
 */
public interface Condition {

    String getSql();

    Object[] getArgs();

    Condition or(Condition condition);

    Condition and(Condition condition);

}
