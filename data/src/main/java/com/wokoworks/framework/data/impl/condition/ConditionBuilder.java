package com.wokoworks.framework.data.impl.condition;

import com.wokoworks.framework.data.Condition;
import lombok.Getter;

import java.util.Objects;

/**
 * @author 0x0001
 */
public final class ConditionBuilder {
    @Getter
    private Condition condition;

    public void where(String where, Object... args) {
        final RawCondition rawCondition = new RawCondition(where, args);
        this.condition = Objects.isNull(this.condition) ? rawCondition : Conditions.and(this.condition, rawCondition);
    }

    public void where(Condition condition) {
        this.condition = Objects.isNull(this.condition) ? condition : Conditions.and(this.condition, condition);
    }

    public boolean hasCondition() {
        return condition != null;
    }
}
