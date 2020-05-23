package com.tangchao.shop.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Class UpdateUserPaymentCodeParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/27 12:47
 * @Version 1.0
 **/
@Data
public class UpdateUserPaymentCodeParam {

    @ApiModelProperty(value = "id", name = "id")
    private Long id;

    @ApiModelProperty(value = "支付宝号或者是微信号", name = "number")
    private String number;

    @ApiModelProperty(value = "真实姓名", name = "username")
    private String username;

    @ApiModelProperty(value = "收款码图片", name = "paymentCodeImg")
    private String paymentCodeImg;

}
