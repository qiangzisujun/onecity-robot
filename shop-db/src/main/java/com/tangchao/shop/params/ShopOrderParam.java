package com.tangchao.shop.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ShopOrderParam {

    @ApiModelProperty(value = "收获人地址id", name = "addressId")
    private Long addressId; // 收获人地址id

    @ApiModelProperty(value = "买家留言", name = "buyerMessage")
    private String buyerMessage;// 买家留言

    @ApiModelProperty(value = "优惠券抵消码", name = "couponCode")
    private String couponCode;// 优惠券抵消码

}
