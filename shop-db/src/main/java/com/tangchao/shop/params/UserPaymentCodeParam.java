package com.tangchao.shop.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Class UserPaymentCodeParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/27 12:19
 * @Version 1.0
 **/
@Data
public class UserPaymentCodeParam {

    @ApiModelProperty(value = "真实姓名", name = "username")
    private String username;

    @ApiModelProperty(value = "支付宝号或者是微信号", name = "number")
    private String number;

    @ApiModelProperty(value = "收款码图片", name = "paymentCodeImg")
    private String paymentCodeImg;

    @ApiModelProperty(value = "收款码类型：1支付宝，2微信", name = "type")
    private Integer type;

}
