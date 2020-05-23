package com.tangchao.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PageResult<T> {
    private Long total;// 总条数
    private int totalPage;// 总页数
    private List<T> items;// 当前页数据
    private BigDecimal totalAmount;// 统计金额

    public PageResult() {
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, BigDecimal totalAmount, List<T> items) {
        this.total = total;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public PageResult(Long total, int totalPage, List<T> items, int pageSize) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
        this.totalPage = (totalPage + pageSize - 1) / pageSize;
    }

    public PageResult(Long total, List<T> items, int pageSize, int totalNum) {
        this.total = total;
        this.items = items;
        this.totalPage = (totalNum + pageSize - 1) / pageSize;
    }
}
