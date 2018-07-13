package com.wokoworks.framework.data;

/**
 * @author 0x0001
 */
public interface Condition {

    /**
     * 获取sql语句
     *
     * @return
     */
    String getSql();

    /**
     * 获取参数列表
     *
     * @return
     */
    Object[] getArgs();

    /**
     * 通过另外一个条件组合成一个or条件
     *
     * @param condition 子条件
     * @return 新的组合条件
     */
    Condition or(Condition condition);

    /**
     * 通过另外一个条件组合为一个and条件
     *
     * @param condition 子条件
     * @return 新的组合条件
     */
    Condition and(Condition condition);

}
