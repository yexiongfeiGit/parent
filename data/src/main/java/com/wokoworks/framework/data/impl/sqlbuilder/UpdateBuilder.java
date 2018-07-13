package com.wokoworks.framework.data.impl.sqlbuilder;

import com.google.common.base.Preconditions;
import com.wokoworks.framework.commons.vo.Pair;
import com.wokoworks.framework.data.Condition;
import com.wokoworks.framework.data.impl.JdbcTemplateUtils;
import com.wokoworks.framework.data.impl.condition.ConditionBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 0x0001
 */
@Slf4j
public class UpdateBuilder<T> {

    private final List<FieldValue> fieldValueList = new ArrayList<>();
    private final ConditionBuilder conditionBuilder = new ConditionBuilder();
    private final String tableName;
    private final JdbcTemplateUtils<T> jdbcTemplateUtils;
    private int limit;

    private final List<Object> args = new ArrayList<>();

    public UpdateBuilder(SqlExecuteProvider<T> sqlExecuteProvider) {
        tableName = sqlExecuteProvider.getTableName();
        jdbcTemplateUtils = sqlExecuteProvider.getJdbcTemplateUtils();
    }

    public UpdateBuilder<T> set(String field, Object value) {
        fieldValueList.add(new FieldValue(field, false, value));
        return this;
    }

    public UpdateBuilder<T> setRawField(String raw) {
        fieldValueList.add(new FieldValue(raw, true, null));
        return this;
    }

    public UpdateBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    public UpdateBuilder<T> where(String where, Object... args) {
        conditionBuilder.where(where, args);
        return this;
    }

    public UpdateBuilder<T> where(Condition condition) {
        conditionBuilder.where(condition);
        return this;
    }

    public int update() {
        return jdbcTemplateUtils.update(getSql(), getArgs());
    }

    private CharSequence getSql() {
        Preconditions.checkArgument(!fieldValueList.isEmpty());

        args.clear();
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET");
        boolean fieldFirst = true;
        for (FieldValue fieldValue : fieldValueList) {
            if (!fieldFirst) {
                sql.append(",");
            }
            if (fieldValue.isRaw()) {
                sql.append(" ").append(fieldValue.getField());
            } else {
                sql.append(" ").append(fieldValue.getField()).append(" = ?");
                args.add(fieldValue.value);
            }
            fieldFirst = false;
        }

        // append where
        final Optional<Pair<String, List<Object>>> pairOptional = SqlBuildUtil.buildCondition(conditionBuilder);
        if (!pairOptional.isPresent()) {
            throw new IllegalStateException("update data but not exists condition");
        }
        pairOptional.ifPresent(p -> SqlBuildUtil.append(sql, args, p));

        // append limit
        SqlBuildUtil.limit(0, limit).ifPresent(p -> SqlBuildUtil.append(sql, args, p));

        log.debug("sql: {}", sql);

        return sql;
    }

    private Object[] getArgs() {
        return args.toArray();
    }

    @Data
    private class FieldValue {
        private final String field;
        private final boolean raw;
        private final Object value;
    }
}
