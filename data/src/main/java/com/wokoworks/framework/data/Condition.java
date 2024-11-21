package com.wokoworks.framework.data;

import com.wokoworks.framework.data.impl.condition.Conditions;

import java.util.Objects;

/**
 * @author 0x0001
 */
public interface Condition {

    /**
     * Obtain sql Sentence
     *
     * @return Corresponding conditions sql Clip
     */
    String getSql();

    /**
     * List of parameters
     *
     * @return List of parameters required by the corresponding conditions
     */
    Object[] getArgs();

    /**
     * Two conditions for execution and Stitch
     *
     * @param condition Second condition
     * @return
     */
    default Condition and(Condition condition) {
        return Conditions.and(this, Objects.requireNonNull(condition));
    }

    /**
     * Two conditions for execution or Stitch
     *
     * @param condition Second condition
     * @return
     */
    default Condition or(Condition condition) {
        return Conditions.or(this, Objects.requireNonNull(condition));
    }

}
