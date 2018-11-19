package com.wokoworks.framework.data.impl.sqlbuilder;

import com.wokoworks.framework.commons.tuple.Tuple;
import com.wokoworks.framework.data.Condition;
import com.wokoworks.framework.data.Sort;
import com.wokoworks.framework.data.impl.condition.ConditionBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class SqlBuildUtil {
    private SqlBuildUtil() {
        throw new UnsupportedOperationException();
    }

    static Optional<Tuple.TwoTuple<String, List<Object>>> buildCondition(ConditionBuilder conditionBuilder) {
        if (conditionBuilder.hasCondition()) {
            final StringBuilder sql = new StringBuilder();
            final List<Object> args = new ArrayList<>();
            final Condition condition = conditionBuilder.getCondition();
            sql.append(" WHERE ");
            sql.append(condition.getSql());
            Collections.addAll(args, condition.getArgs());
            return Optional.of(Tuple.of(sql.toString(), args));
        }
        return Optional.empty();
    }

    static Optional<Tuple.TwoTuple<String, List<Object>>> limit(int offset, int limit) {
        if (limit > 0) {
            final StringBuilder sql = new StringBuilder();
            List<Object> args = new ArrayList<>();
            if (offset > 0) {
                sql.append(" LIMIT ?, ?");
                args.add(offset);
                args.add(limit);
            } else {
                sql.append(" LIMIT ?");
                args.add(limit);
            }
            return Optional.of(Tuple.of(sql.toString(), args));
        }
        return Optional.empty();
    }

    static Optional<String> orderBy(List<Sort> sorts) {
        if (!sorts.isEmpty()) {
            StringBuilder sql = new StringBuilder();
            sql.append(" ORDER BY ");

            for (Sort sort : sorts) {
                sql.append(sort.getColumnName());
                switch (sort.getDirection()) {
                    case ASC:
                        sql.append(" ASC");
                        break;
                    case DESC:
                        sql.append(" DESC");
                        break;
                    default:
                        throw new IllegalStateException("not support sort direction");
                }
                sql.append(',');
            }
            // delete last char ','
            sql.setLength(sql.length() - 1);
            return Optional.of(sql.toString());
        }
        return Optional.empty();
    }

    static void append(StringBuilder sqlBuilder, List<Object> args, Tuple.TwoTuple<String, List<Object>> pair) {
        sqlBuilder.append(pair.first);
        args.addAll(pair.second);
    }
}
