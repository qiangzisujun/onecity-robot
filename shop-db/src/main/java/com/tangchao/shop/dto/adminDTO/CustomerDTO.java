package com.tangchao.shop.dto.adminDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/30 21:37
 */
@ApiModel
@Data
public class CustomerDTO {

    @ApiModelProperty(value = "昵称/会员名称",name = "userRealName")
    private String userRealName;

    @ApiModelProperty(value = "会员电话",name = "phone")
    private String phone;

    @ApiModelProperty(value = "邀请码",name = "inviteNo")
    private String inviteCode;

    @ApiModelProperty(value = "邀请人",name = "inviteName")
    private String inviteId;

    @ApiModelProperty(value = "注册开始时间/审核开始时间",name = "registerStartTime")
    private String registerStartTime;

    @ApiModelProperty(value = "注册结束时间/审核结束时间",name = "registerEndTime")
    private String registerEndTime;

    @ApiModelProperty(value = "类型(1:会员，2：机器人,3:充值会员)/1:充值,2消费,3:佣金提现,4佣金充值",name = "typeId")
    private Integer typeId;

    @ApiModelProperty(value = "状态（0 正常   1 已拉黑）/支付方式(1:支付宝,2:微信,3:余额,4:后台,5:代理,6:银行,7:佣金,8:虚拟卡充值,9:不中全返,10:支付猫)",name = "status")
    private Integer status;

    @ApiModelProperty(value = "页数",name = "pageNo")
    private Integer pageNo;

    @ApiModelProperty(value = "页数大小",name = "pageSize")
    private Integer pageSize;

    @ApiModelProperty(value = "购物订单号",name = "orderNo")
    private String orderNo;

    private Integer type;

    @ApiModelProperty(value = "申请时间开始时间",name = "applyStartTime")
    private String applyStartTime;

    @ApiModelProperty(value = "申请时间结束时间",name = "applyEndTime")
    private String applyEndTime;

    private String stageId;

    @ApiModelProperty(value = "商品名称",name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "购买时间段(0白天，1晚上)",name = "byDate")
    private String byDate;
}
