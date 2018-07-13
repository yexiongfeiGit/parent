package com.wokoworks.framework.data.impl.sqlbuilder;

import com.google.common.base.Preconditions;
import com.wokoworks.framework.data.Condition;
import com.wokoworks.framework.data.Page;
import com.wokoworks.framework.data.Sort;
import com.wokoworks.framework.data.impl.JdbcTemplateUtils;
import com.wokoworks.framework.data.impl.condition.ConditionBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 0x0001
 */
@Slf4j
public class SelectBuilder<T> {
    private final List<String> fields = new ArrayList<>();
    private final List<Sort> sorts = new ArrayList<>();
    private final ConditionBuilder conditionBuilder = new ConditionBuilder();

    private final List<Object> args = new ArrayList<>();
    private final String tableName;
    private final JdbcTemplateUtils<T> jdbcTemplateUtils;

    private int offset;
    private int limit;


    public SelectBuilder(SqlExecuteProvider<T> sqlExecuteProvider) {
        tableName = sqlExecuteProvider.getTableName();
        jdbcTemplateUtils = sqlExecuteProvider.getJdbcTemplateUtils();
    }

    public SelectBuilder<T> field(String... fields) {
        Preconditions.checkNotNull(fields);
        Collections.addAll(this.fields, fields);
        return this;
    }

    public SelectBuilder<T> where(String where, Object... args) {
        conditionBuilder.where(where, args);
        return this;
    }

    public SelectBuilder<T> where(Condition condition) {
        conditionBuilder.where(condition);
        return this;
    }

    public SelectBuilder<T> offset(int offset) {
        this.offset = offset;
        return this;
    }

    public SelectBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SelectBuilder<T> orderBy(Sort... sorts) {
        Preconditions.checkNotNull(sorts);
        Collections.addAll(this.sorts, sorts);
        return this;
    }

    public SelectBuilder<T> orderBy(Collection<Sort> sorts) {
        Preconditions.checkNotNull(sorts);
        this.sorts.addAll(sorts);
        return this;
    }

    public Optional<T> findOne() {
        this.limit(1);
        final CharSequence sql = getSql();
        return jdbcTemplateUtils.findOne(sql, getArgs());
    }

    public List<T> find() {
        final CharSequence sql = getSql();
        return jdbcTemplateUtils.find(sql, getArgs());
    }

    public int findCount() {
        List<String> origFields = new ArrayList<>(this.fields);
        fields.clear();
        fields.add("COUNT(*)");
        final int originLimit = limit;
        this.limit(0);
        final CharSequence sql = getSql();
        try {
            return jdbcTemplateUtils.findCount(sql, getArgs());
        } finally {
            this.fields.clear();
            this.fields.addAll(origFields);
            this.limit(originLimit);
        }
    }

    public Page<T> page(int pageNo, int pageSize) {
        Preconditions.checkArgument(pageNo > 0, "page no must be greater then 0");

        final int count = this.findCount();
        final int pageOffset = pageSize * (pageNo - 1);
        final int originOffset = offset;
        final int originLimit = limit;
        try {
            this.offset(pageOffset);
            this.limit(pageSize);
            final List<T> data = find();
            return new Page<>(count, pageSize, pageNo, data);
        } finally {
            this.offset = originOffset;
            this.limit = originLimit;
        }
    }

    // ----

    private CharSequence getSql() {
        args.clear();
        StringBuilder sql = new StringBuilder("SELECT ");
        if (this.fields.isEmpty()) {
            sql.append(" *");
        } else {
            sql.append(this.fields.stream().collect(Collectors.joining(",")));
        }
        sql.append(" FROM ").append(tableName);

        // append where
        SqlBuildUtil.buildCondition(conditionBuilder)
            .ifPresent(p -> SqlBuildUtil.append(sql, args, p));

        // append order by
        SqlBuildUtil.orderBy(sorts).ifPresent(sql::append);

        // append limit
        SqlBuildUtil.limit(offset, limit).ifPresent(p -> SqlBuildUtil.append(sql, args, p));

        log.debug("sql: {}", sql);

        return sql;
    }

    private Object[] getArgs() {
        return args.toArray();
    }


}