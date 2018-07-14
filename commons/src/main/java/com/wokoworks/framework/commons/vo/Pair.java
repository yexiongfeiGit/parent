package com.wokoworks.framework.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 0x0001
 */
@AllArgsConstructor
@Getter
public class Pair<F, S> {
    private final F first;
    private final S second;

    public static  <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

}
