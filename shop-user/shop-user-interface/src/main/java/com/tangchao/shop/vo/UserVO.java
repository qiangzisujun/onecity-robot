package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserVO {

    @ApiModelProperty(value = "密码", name = "password")
    private String password;// 密码

    @ApiModelProperty(value = "电话-用户名", name = "phone")
    private String phone;// 电话-用户名

    @ApiModelProperty(value = "验证码", name = "code")
    private String code;

    @ApiModelProperty(value = "邀请码", name = "inviteId")
    private String inviteId;
}
