package com.wokoworks.framework.data.impl.sqlbuilder;

import com.wokoworks.framework.data.impl.JdbcTemplateUtils;

/**
 * @author 0x0001
 */
public interface SqlExecuteProvider<T> {
    String getTableName();

    JdbcTemplateUtils<T> getJdbcTemplateUtils();
}
