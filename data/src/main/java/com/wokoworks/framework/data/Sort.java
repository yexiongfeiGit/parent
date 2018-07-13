package com.wokoworks.framework.data;

import lombok.Getter;

/**
 * @author 0x0001
 */
@Getter
public class Sort {

    private final String columnName;
    private final Direction direction;

    public Sort(String columnName, Direction direction) {
        this.columnName = columnName;
        this.direction = direction;
    }

    public static Sort of(String columnName, Direction direction) {
        return new Sort(columnName, direction);
    }

    public enum Direction {
        ASC, DESC
    }
}
