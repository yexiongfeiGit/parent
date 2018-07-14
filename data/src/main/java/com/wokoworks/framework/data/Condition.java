package com.wokoworks.framework.data;

/**
 * @author 0x0001
 */
public interface Condition {

    /**
     * 获取sql语句
     *
     * @return 对应条件生成的sql片段
     */
    String getSql();

    /**
     * 获取参数列表
     *
     * @return 对应条件需要的参数列表
     */
    Object[] getArgs();

}
