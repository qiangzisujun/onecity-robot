package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Table(name = "capitalDetailsVO")
@Data
public class CapitalDetailsVO {

    @ApiModelProperty(value = "消费总额", name = "payAmountSum")
    @Transient
    private Double payAmountSum;

    @ApiModelProperty(value = "充值总额", name = "rechargeamountSum")
    @Transient
    private Double rechargeamountSum;

    @ApiModelProperty(value = "消费/赠送福分", name = "score")
    @Transient
    private Double score;

    @ApiModelProperty(value = "会员唯一标识", name = "userCode")
    @Transient
    private Long userCode;

    @ApiModelProperty(value = "余额", name = "userMoney")
    @Transient
    private Double userMoney;




}
