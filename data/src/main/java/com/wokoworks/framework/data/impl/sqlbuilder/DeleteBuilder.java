package com.wokoworks.framework.data.impl.sqlbuilder;

import com.google.common.base.Preconditions;
import com.wokoworks.framework.commons.vo.Pair;
import com.wokoworks.framework.data.Condition;
import com.wokoworks.framework.data.Sort;
import com.wokoworks.framework.data.impl.JdbcTemplateUtils;
import com.wokoworks.framework.data.impl.condition.ConditionBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author 0x0001
 */
@Slf4j
public class DeleteBuilder<T> {
    private final ConditionBuilder conditionBuilder = new ConditionBuilder();
    private final List<Sort> sorts = new ArrayList<>();

    private final List<Object> args = new ArrayList<>();
    private final String tableName;
    private final JdbcTemplateUtils<T> jdbcTemplateUtils;

    private int offset;
    private int limit;

    public DeleteBuilder(SqlExecuteProvider<T> sqlExecuteProvider) {
        tableName = sqlExecuteProvider.getTableName();
        jdbcTemplateUtils = sqlExecuteProvider.getJdbcTemplateUtils();
    }

    public DeleteBuilder<T> orderBy(Sort... sorts) {
        Preconditions.checkNotNull(sorts);
        Collections.addAll(this.sorts, sorts);
        return this;
    }

    public DeleteBuilder<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public DeleteBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public DeleteBuilder<T> where(String where, Object... args) {
        conditionBuilder.where(where, args);
        return this;
    }

    public DeleteBuilder<T> where(Condition condition) {
        conditionBuilder.where(condition);
        return this;
    }

    public int delete() {
        return jdbcTemplateUtils.update(getSql(), getArgs());
    }

    private CharSequence getSql() {
        args.clear();
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName);

        // append where
        final Optional<Pair<String, List<Object>>> pair = SqlBuildUtil.buildCondition(conditionBuilder);
        pair.ifPresent(p -> SqlBuildUtil.append(sql, args, p));

        // append sort
        SqlBuildUtil.orderBy(sorts).ifPresent(sql::append);

        // append limit
        SqlBuildUtil.limit(offset, limit)
            .ifPresent(p -> SqlBuildUtil.append(sql, args, p));

        log.debug("sql: {}", sql);

        return sql;
    }

    private Object[] getArgs() {
        return args.toArray();
    }

}
