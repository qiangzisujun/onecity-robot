package com.tangchao.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 商品已开奖
 */
@JsonIgnoreProperties(value = {"cause","stackTrace","localizedMessage","suppressed","message"})
@Data
public class GoodsExpiredException extends RuntimeException {

    //  商品编号
    private Long goodsNo;

    //  商品名称
    private String goodsName;

    //  期次Id
    private Long stageId;

    public GoodsExpiredException() {
        super();
    }

    public GoodsExpiredException(Long goodsNo, String goodsName, Long stageId) {
        this.goodsNo = goodsNo;
        this.goodsName = goodsName;
        this.stageId = stageId;
    }
}
