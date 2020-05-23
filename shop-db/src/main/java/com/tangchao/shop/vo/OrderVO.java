package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel
@Data
public class OrderVO {


    @ApiModelProperty(value = "商品列表(json格式)", name = "goodsList")
    private String goodsList;

    @ApiModelProperty(value = "用户福分", name = "userScore")
    private Double userScore;

    @ApiModelProperty(value = "用户余额", name = "userBalance")
    private Double userBalance;

    @ApiModelProperty(value = "订单编号", name = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "消费积分", name = "consumeScore")
    private String consumeScore;

    @ApiModelProperty(value = "满足福分抵扣", name = "scoreConfArray")
    private Double score;

    @ApiModelProperty(value = "满足福分抵扣金额", name = "scoreConfArray")
    private Double scoreMoney;

    @ApiModelProperty(value = "商品总金额", name = "totalMoney")
    private Double totalMoney;

    @ApiModelProperty(value = "是否为活动商品(0否 1是)", name = "isActivity")
    private Integer isActivity;

    @ApiModelProperty(value = "积分抵扣金额", name = "scoreDeductionMoney")
    private Double scoreDeductionMoney;

    @ApiModelProperty(value = "合计", name = "scoreDeductionMoney")
    private Double orderTotal;

    @ApiModelProperty(value = "会员使用福分抵扣订单需满足", name = "scoreDeductionMoney")
    private Double needMoney;


}
