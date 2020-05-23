package com.tangchao.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 库存不足异常
 */
@JsonIgnoreProperties(value = {"cause","stackTrace","localizedMessage","suppressed","message"})
public class LackStockException extends RuntimeException {

    //  商品编号
    private Long goodsNo;

    //  商品名称
    private String goodsName;

    //  期次Id
    private Long stageId;

    //  购买数量
    private Integer payNum;

    //  库存
    private Integer goodsStock;

    private LackStockException(){
        super();
    }


    public LackStockException(Long goodsNo, String goodsName, Long stageId, Integer payNum, Integer goodsStock) {
        this.goodsNo = goodsNo;
        this.goodsName = goodsName;
        this.stageId = stageId;
        this.payNum = payNum;
        this.goodsStock = goodsStock;
    }



    /* -------------------------------------------------------------------------------------------------------------- */

    public String getGoodsName() {
        return goodsName;
    }

    public Long getGoodsNo() {
        return goodsNo;
    }

    public Long getStageId() {
        return stageId;
    }

    public Integer getPayNum() {
        return payNum;
    }

    public Integer getGoodsStock() {
        return goodsStock;
    }
}
