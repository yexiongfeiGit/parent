package com.wokoworks.framework.data.impl.sqlbuilder;

import com.wokoworks.framework.data.impl.BaseRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 0x0001
 */
@Slf4j
public final class InsertBuilder<T, K> {
    private final BaseRepositoryImpl<T, K> repository;
    private final String idColumnName;
    private final String tableName;
    private final TableInfo<T, K> tableInfo;
    private final JdbcTemplate jdbcTemplate;

    private boolean ignoreIdColumn = true;

    public InsertBuilder(BaseRepositoryImpl<T, K> repository) {
        this.repository = repository;
        idColumnName = this.repository.getIdColumnName();
        tableName = this.repository.getTableName();
        tableInfo = TableInfo.newTableInfo(repository);
        jdbcTemplate = this.repository.getJdbcTemplate();
    }

    public InsertBuilder<T, K> setIgnoreIdColumn(boolean ignoreIdColumn) {
        this.ignoreIdColumn = ignoreIdColumn;
        return this;
    }

    public int insert(T data, KeyHolder keyHolder) {
        final List<String> columnNames = tableInfo.getEffectiveColumnNames();
        final String sql = generatorSql(columnNames);
        final Object[] args = generatorArgs(data, columnNames);

        if (keyHolder == null) {
            return jdbcTemplate.update(sql, args);
        } else {
            log.debug("sql: {}", sql);
            return jdbcTemplate.update(new GeneratorKeyPreparedStatementCreator(sql, args), keyHolder);
        }
    }

    public int[] batch(Collection<T> data, KeyHolder keyHolder) {
        final List<String> columnNames = tableInfo.getEffectiveColumnNames();
        final String sql = generatorSql(columnNames);
        final List<Object[]> argList = new ArrayList<>(data.size());
        for (T d : data) {
            argList.add(generatorArgs(d, columnNames));
        }
        if (keyHolder == null) {
            return jdbcTemplate.batchUpdate(sql, argList);
        } else {
            // key holder generator
            log.debug("sql: {}", sql);
            return jdbcTemplate.execute(new BatchGeneratorKeyConnectionCallback(sql, argList, keyHolder));
        }
    }

    // ----------------------------------------------------- private methods

    private String generatorSql(List<String> columnNames) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ").append(tableName).append("(");

        for (String columnName : columnNames) {
            if (ignoreIdColumn) {
                // skip id column
                if (columnName.equalsIgnoreCase(idColumnName)) {
                    continue;
                }
            }
            sqlBuilder.append(columnName).append(",");
        }
        sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')');
        sqlBuilder.append(" VALUES (");
        for (String columnName : columnNames) {
            // skip id column
            if (columnName.equalsIgnoreCase(idColumnName)) {
                continue;
            }
            sqlBuilder.append("?,");
        }
        sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')');
        return sqlBuilder.toString();
    }

    private Object[] generatorArgs(T data, List<String> columnNames) {
        List<Object> args = new ArrayList<>(columnNames.size());
        for (String columnName : columnNames) {
            if (ignoreIdColumn) {
                // skip id column
                if (columnName.equalsIgnoreCase(idColumnName)) {
                    continue;
                }
            }
            args.add(tableInfo.getValueByColumnName(data, columnName));
        }
        return args.toArray();
    }

    // --------------------------------------------------

    static class GeneratorKeyPreparedStatementCreator implements PreparedStatementCreator {

        private final String sql;
        private final Object[] args;

        GeneratorKeyPreparedStatementCreator(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (Object arg : args) {
                preparedStatement.setObject(i++, arg);
            }
            return preparedStatement;
        }
    }

    static class BatchGeneratorKeyConnectionCallback implements ConnectionCallback<int[]> {

        private final String sql;
        private final List<Object[]> argList;
        private final KeyHolder keyHolder;

        BatchGeneratorKeyConnectionCallback(String sql, List<Object[]> argList, KeyHolder keyHolder) {
            this.sql = sql;
            this.argList = argList;
            this.keyHolder = keyHolder;
        }

        @Override
        public int[] doInConnection(@Nonnull Connection conn) throws SQLException, DataAccessException {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (Object[] args : argList) {
                int i = 1;
                for (Object arg : args) {
                    ps.setObject(i++, arg);
                }
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            final List<Map<String, Object>> generatedKeys = keyHolder.getKeyList();
            final ResultSet keys = ps.getGeneratedKeys();
            if (keys != null) {
                try {
                    RowMapperResultSetExtractor<Map<String, Object>> rse =
                        new RowMapperResultSetExtractor<>(new ColumnMapRowMapper(), 1);
                    generatedKeys.addAll(rse.extractData(keys));
                } finally {
                    JdbcUtils.closeResultSet(keys);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("SQL update affected  {} rows and returned {} keys", results, generatedKeys.size());
            }
            return results;
        }
    }


}
