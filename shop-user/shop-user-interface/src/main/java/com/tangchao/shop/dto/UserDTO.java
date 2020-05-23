package com.tangchao.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UserDTO {

    @ApiModelProperty(value = "登陆签名", name = "token")
    private String token;

    @ApiModelProperty(value = "昵称", name = "nickName")
    private String nickName;

    @ApiModelProperty(value = "头像地址", name = "avatarUrl")
    private String avatarUrl;

    @ApiModelProperty(value = "用户积分", name = "integral")
    private Long integral;

    @ApiModelProperty(value = "个性签名", name = "signature")
    private String signature;

    @ApiModelProperty(value = "用户标识", name = "userCode")
    private String userCode;

    @ApiModelProperty(value = "用户身份(是否为充值会员{ 0：不是，1：是 })", name = "userIdentity")
    private String userIdentity;
}
