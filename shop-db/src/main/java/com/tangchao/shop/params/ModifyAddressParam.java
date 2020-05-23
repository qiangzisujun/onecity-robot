package com.tangchao.shop.params;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Class ModifyAddressParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/15 17:02
 * @Version 1.0
 **/
@Data
@ApiModel
public class ModifyAddressParam {

    @ApiModelProperty(value = "收获人地址id", name = "addressId")
    private Long addressId; // 收获人地址id

    @ApiModelProperty(value = "订单ID", name = "orderId")
    private String orderId;// 买家留言

    @ApiModelProperty(value = "优惠券抵消码", name = "couponCode")
    private String couponCode;// 优惠券抵消码


}
