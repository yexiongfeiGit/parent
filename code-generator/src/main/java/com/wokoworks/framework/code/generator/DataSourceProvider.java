package com.wokoworks.framework.code.generator;

import javax.sql.DataSource;

/**
 * @author 0x0001
 */
public interface DataSourceProvider {
    DataSource getDataSource(String host, int port, String username, String password);
}
