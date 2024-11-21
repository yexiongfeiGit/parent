package com.wokoworks.framework.data.impl.sqlbuilder;

import com.wokoworks.framework.data.impl.JdbcTemplateUtils;

/**
 * @author 0x0001
 */
public interface SqlExecuteProvider<T> {
    /**
     * Get the corresponding table name
     *
     * @return The name of the table to be operated
     */
    String getTableName();

    /**
     * Obtain jdbc template Operating tool object
     *
     * @return Corresponding jdbc Operating tool object
     */
    JdbcTemplateUtils<T> getJdbcTemplateUtils();
}
