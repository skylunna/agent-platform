package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result;

import lombok.Data;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Data
@Getter
public class PageResult<T> {

    private final int pageNo;
    private final int pageSize;
    private final long total;
    private final List<T> records;

    private PageResult(int pageNo, int pageSize, long total, List<T> list) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.records = list;
    }

    public static <T> PageResult<T> of(int pageNo, int pageSize, long total, List<T> list) {
        return new PageResult<>(pageNo, pageSize, total, list);
    }

    public static <T> PageResult<T> empty(int pageNo, int pageSize) {
        return new PageResult<>(pageNo, pageSize, 0L, Collections.emptyList());
    }
}