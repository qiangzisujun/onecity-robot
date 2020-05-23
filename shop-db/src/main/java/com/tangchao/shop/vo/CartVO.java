package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class CartVO {

    @ApiModelProperty(value = "购物车Id", name = "id")
    private Long id;

    @ApiModelProperty(value = "商品期数Id", name = "stageId")
    private Long stageId;

    @ApiModelProperty(value = "库存", name = "limited")
    private Integer limited;

    @ApiModelProperty(value = "购买价格", name = "buyPrice")
    private Double buyPrice;

    @ApiModelProperty(value = "商品编号", name = "goodsNo")
    private String goodsNo;

    @ApiModelProperty(value = "商品图片", name = "goodsPicture")
    private String goodsPicture;

    @ApiModelProperty(value = "商品名称", name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "数量", name = "payNum")
    private Integer payNum;

    @ApiModelProperty(value = "商品期数Id", name = "goodsStageId")
    private Integer goodsStageId;
}
