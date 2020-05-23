package com.tangchao.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Class CouponLogDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/2/28 9:56
 * @Version 1.0
 **/
@Data
@ApiModel
public class CouponLogDTO {

    @ApiModelProperty(value = "用户电话号码")
    private String userMobile;

    @ApiModelProperty(value = "需支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "是否支付（0/未付款 1/已付款）")
    private Integer logStatus;

    @ApiModelProperty(value = "下单时间")
    private Date createTime;

    @ApiModelProperty(value = "订单流水号")
    private String no;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @ApiModelProperty(value = "优惠券状态")
    private Integer couponStatus;

    @ApiModelProperty(value = "优惠券抵消码")
    private String couponCode;

    @ApiModelProperty(value = "优惠券可抵消金额")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "优惠券有效期")
    private Date efectiveTime;

}
