package com.wokoworks.framework.data;

import com.wokoworks.framework.data.impl.condition.Conditions;

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

    /**
     * 两个条件执行and拼接
     * @param condition 第二个条件
     * @return
     */
    default Condition and(Condition condition) {
        return Conditions.and(this, condition);
    }

    /**
     * 两个条件执行or拼接
     * @param condition 第二个条件
     * @return
     */
    default Condition or(Condition condition) {
        return Conditions.or(this, condition);
    }

}
