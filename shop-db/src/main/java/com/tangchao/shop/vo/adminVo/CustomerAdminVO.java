package com.tangchao.shop.vo.adminVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/31 16:11
 */
@ApiModel
@Data
public class CustomerAdminVO {

    @ApiModelProperty(value = "用户id",name = "id")
    private String id;

    @ApiModelProperty(value = "用户名",name = "userName")
    private String userName;

    @ApiModelProperty(value = "用户头像",name = "userPortrait")
    private String userPortrait;

    @ApiModelProperty(value = "真实姓名",name = "userRealName")
    private String userRealName;

    @ApiModelProperty(value = "邀请人",name = "inviteId")
    private String inviteId;

    @ApiModelProperty(value = "手机号码",name = "userMobile")
    private String userMobile;

    @ApiModelProperty(value = "是否为机器人{ 0：不是，1：是 }",name = "isRobot")
    private Integer isRobot;

    @ApiModelProperty(value = "是否为充值会员{ 0：不是，1：是 }",name = "isSupplier")
    private Integer isSupplier;

    @ApiModelProperty(value = "邮箱",name = "userEmail")
    private String userEmail;
}
