package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class GoodsStageVO {

    @ApiModelProperty(value = "商品期数id", name = "id")
    private Long id;

    @ApiModelProperty(value = "商品唯一编码", name = "goodsNo")
    private String goodsNo;

    @ApiModelProperty(value = "商品名称", name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "商品价格", name = "goodsPrice")
    private Double goodsPrice;


    @ApiModelProperty(value = "商品图片url", name = "buySize")
    private String goodsPicture;

    @ApiModelProperty(value = "每人限购次数", name = "buyPrice")
    private Integer buyNum;


    @ApiModelProperty(value = "每次购买价格", name = "buyPrice")
    private Double buyPrice;


    @ApiModelProperty(value = "购买总数", name = "buySize")
    private Integer buySize;

    @ApiModelProperty(value = "当前购买数", name = "buyIndex")
    private Integer buyIndex;

    @ApiModelProperty(value = "商品是否收藏(0:不是,1是)", name = "isCollection")
    private Integer isCollection;
}
