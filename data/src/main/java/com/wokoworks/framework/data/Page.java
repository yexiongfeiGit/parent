package com.wokoworks.framework.data;

import lombok.Data;

import java.util.List;

/**
 * @author 0x0001
 */
@Data
public class Page<T> {
    private final List<T> data;
    private final int currentPageNo;
    private final int totalCount;
    private final int totalPage;
    private final int pageSize;

    public Page(int totalCount, int pageSize, int currentPageNo, List<T> data) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currentPageNo = currentPageNo;
        this.data = data;
        this.totalPage = totalCount % pageSize > 0 ? totalCount / pageSize + 1 : totalCount / pageSize;
    }
}
