package com.wokoworks.framework.data.impl.condition;

import com.wokoworks.framework.data.Condition;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
class RawCondition implements Condition {

    private final String condition;
    @Getter
    private final Object[] args;

    @Override
    public String getSql() {
        return condition;
    }
}