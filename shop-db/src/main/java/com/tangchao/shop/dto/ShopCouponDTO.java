package com.tangchao.shop.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel
public class ShopCouponDTO {

    @ApiModelProperty(value = "优惠券名称", name = "goodsName")
    private String couponName;//优惠券名称

    @ApiModelProperty(value = "优惠券图片", name = "img")
    private String img;//优惠券图片

    @ApiModelProperty(value = "优惠券名称", name = "detailImg")
    private String detailImg;//优惠券详情图

    @ApiModelProperty(value = "优惠券名称", name = "efectiveTime")
    private int efectiveTime;//有效时间（天）

    @ApiModelProperty(value = "优惠券名称", name = "couponAmount")
    private BigDecimal couponAmount;//优惠券金额

    @ApiModelProperty(value = "优惠券名称", name = "num")
    private int num;//发放数量（-1则表示无限，或者填写具体数量）

    @ApiModelProperty(value = "优惠券购买金额", name = "purchaseAmount")
    private BigDecimal purchaseAmount;//优惠券购买金额

    @ApiModelProperty(value = "获得抽奖次数", name = "luckdrawNum")
    private int luckdrawNum;//获得抽奖次数

    @ApiModelProperty(value = "排序 数字越小越靠前", name = "sort")
    private int sort;//排序 数字越小越靠前

    @ApiModelProperty(value = "使用说明", name = "explain")
    private String description;//使用说明

    @ApiModelProperty(value = "操作人(不需要填)", name = "userId")
    private Long userId;//使用说明


    @ApiModelProperty(value = "操作人(不需要填)", name = "userId")
    private Long id;//使用说明
}
