package com.wokoworks.framework.code.generator;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 0x0001
 */
@Component
public class DefaultDataSourceProvider implements DataSourceProvider {
    private Map<ConfigProps, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    @Override
    public DataSource getDataSource(String host, int port, String username, String password) {
        final ConfigProps config = new ConfigProps(host, port, username, password);
        return getDataSource(config);
    }

    private DataSource getDataSource(ConfigProps config) {
        return dataSourceMap.computeIfAbsent(config, c -> DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .driverClassName(Driver.class.getName())
            .url(c.toJdbcUrl())
            .username(c.getUsername())
            .password(c.getPassword())
            .build());
    }

    @Data
    @AllArgsConstructor
    private static class ConfigProps {
        private String host;
        private int port;
        private String username;
        private String password;

        String toJdbcUrl() {
            return String.format("jdbc:mysql://%s:%s", host, port);
        }
    }
}
