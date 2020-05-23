package com.tangchao.shop.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Class GetCouponParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020.2.25 17:46
 * @Version 1.0
 **/
@Data
public class GetCouponParam {

    @ApiModelProperty(value = "优惠券ID", name = "id")
    private Integer couponId;

    @ApiModelProperty(value = "赠送人userCode", name = "giveUserCode")
    private Long giveUserCode;

}
