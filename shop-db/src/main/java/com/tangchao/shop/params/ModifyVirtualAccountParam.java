package com.tangchao.shop.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Class ModifyVirtualAccountParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/4/2 15:49
 * @Version 1.0
 **/
@Data
public class ModifyVirtualAccountParam {

    @ApiModelProperty(value = "虚拟账号", name = "virtualAccount")
    private String virtualAccount;// 买家留言

    @ApiModelProperty(value = "订单ID", name = "orderId")
    private String orderId;// 买家留言

}
