package com.wokoworks.framework.data.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author 0x0001
 */
public class JdbcTemplateUtils<T> {
    @Setter
    @Getter
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<T> rowMapper;

    public JdbcTemplateUtils(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public JdbcTemplateUtils(JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public final int findCount(CharSequence sql, Object... args) {
        Integer count = jdbcTemplate.query(sql.toString(), args, rs -> {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        });
        return Optional.ofNullable(count).orElse(0);
    }


    public final List<T> find(CharSequence sql, Object... args) {
        return jdbcTemplate.query(sql.toString(), args, rowMapper);
    }

    public final Optional<T> findOne(CharSequence sql, Object... args) {
        return jdbcTemplate.query(sql.toString(), args, rs -> {
            if (rs.next()) {
                final T data = rowMapper.mapRow(rs, 0);
                return Optional.ofNullable(data);
            }
            return Optional.empty();
        });
    }

    public final int update(CharSequence sql, Object... args) {
        return jdbcTemplate.update(sql.toString(), args);
    }


}
