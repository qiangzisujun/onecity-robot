package com.tangchao.shop.dto.adminDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 13:09
 */
@Data
@ApiModel
public class ManagerUserDTO {


    @ApiModelProperty(value = "用户名",name = "userName")
    private String userName;

    @ApiModelProperty(value = "密码",name = "password")
    private String password;

    @ApiModelProperty(value = "验证码",name = "code")
    private String code;

    /*@ApiModelProperty(value = "是否记住登录(true:记住登录,false:不记住登录)",name = "rememberMe")
    private Boolean rememberMe;*/
}
