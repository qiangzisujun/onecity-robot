package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class OrderResponse {

    @ApiModelProperty(value = "订单ID", name = "订单ID")
    private String OrderId;

    @ApiModelProperty(value = "订单编号", name = "订单编号")
    private String OrderNo;

    @ApiModelProperty(value = "金额", name = "金额")
    private Long total;

    @ApiModelProperty(value = "微信下单返回信息", name = "微信下单返回信息")
    private UnifiedOrderResponse unifiedOrderResponse;

    private Object wxReslt;

}