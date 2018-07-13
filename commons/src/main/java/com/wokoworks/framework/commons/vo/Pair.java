package com.wokoworks.framework.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 0x0001
 */
@AllArgsConstructor
@Getter
public class Pair<First, Second> {
    private final First first;
    private final Second second;

    public static  <First, Second> Pair<First, Second> of(First first, Second second) {
        return new Pair<>(first, second);
    }

}
