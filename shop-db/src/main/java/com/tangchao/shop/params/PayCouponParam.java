package com.tangchao.shop.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Class PayCouponParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020.2.15 19:22
 * @Version 1.0
 **/
@Data
public class PayCouponParam {

    @ApiModelProperty(value = "优惠券ID", name = "id")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券数量", name = "number")
    private Integer number;

}
