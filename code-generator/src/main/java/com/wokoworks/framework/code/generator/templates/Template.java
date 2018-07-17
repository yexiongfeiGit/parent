package com.wokoworks.framework.code.generator.templates;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author 0x0001
 */
public interface Template<T> {
    /**
     * 模板处理方法
     * @param model
     * @param out
     */
    void process(T model, OutputStream out) throws IOException;
}
