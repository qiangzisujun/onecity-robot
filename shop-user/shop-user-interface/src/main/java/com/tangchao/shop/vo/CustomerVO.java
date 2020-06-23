package com.tangchao.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel
public class CustomerVO implements Serializable {


    private String isShopping;
    private Long id;

    @ApiModelProperty(value = "用户账号", name = "userMobile")
    private String userMobile;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Long userCode;


    @ApiModelProperty(value = "是否公开拼团记录{0：公开，1：隐藏}", name = "isCollageRecord")
    private Integer isCollageRecord;

    @ApiModelProperty(value = "是否公开获得的商品{0：公开，1：隐藏}", name = "isObtainGoods")
    private Integer isObtainGoods;

    @ApiModelProperty(value = "是否公开晒单{0：公开，1：隐藏}", name = "isShowOrder")
    private Integer isShowOrder;

    @ApiModelProperty(value = "用户头像", name = "userPortrait")
    private String userPortrait;

    @ApiModelProperty(value = "用户名", name = "userName")
    private String userName;

    @ApiModelProperty(value = "余额", name = "Balance")
    private Long Balance;

    @ApiModelProperty(value = "福分", name = "Balance")
    private Double integral;

    @ApiModelProperty(value = "是否为充值会员{ 0：不是，1：是 }", name = "isSupplier")
    private Integer isSupplier;

    @ApiModelProperty(value = "连续签到天数", name = "signInDays")
    private Integer signInDays;
}
