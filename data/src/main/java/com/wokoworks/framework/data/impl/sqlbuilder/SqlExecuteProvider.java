package com.wokoworks.framework.data.impl.sqlbuilder;

import com.wokoworks.framework.data.impl.JdbcTemplateUtils;

/**
 * @author 0x0001
 */
public interface SqlExecuteProvider<T> {
    /**
     * 获取对应表名
     *
     * @return 要操作的表的名称
     */
    String getTableName();

    /**
     * 获取jdbc template操作工具对象
     *
     * @return 对应的jdbc操作工具对象
     */
    JdbcTemplateUtils<T> getJdbcTemplateUtils();
}
