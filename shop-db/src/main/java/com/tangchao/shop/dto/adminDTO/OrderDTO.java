package com.tangchao.shop.dto.adminDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/31 18:07
 */
@Data
@ApiModel
public class OrderDTO {

    @ApiModelProperty(value = "商品编号",name = "goodsNo")
    private String goodsNo;

    @ApiModelProperty(value = "订单编号",name = "orderNo")
    private String orderNo;

    @ApiModelProperty(value = "用户名/中奖人",name = "userName")
    private String userName;

    @ApiModelProperty(value = "用户电话/中奖会员电话",name = "userMobile")
    private String userMobile;

    @ApiModelProperty(value = "创建开始时间",name = "createStartTime")
    private String createStartTime;

    @ApiModelProperty(value = "创建结束时间",name = "createEndTime")
    private String createEndTime;

    @ApiModelProperty(value = "页数",name = "pageNo")
    private Integer pageNo;

    @ApiModelProperty(value = "页数大小",name = "pageSize")
    private Integer pageSize;

    @ApiModelProperty(value = "订单状态{ -4:订单超时，-3:待确认订单，-2：未付款订单，-1：用户取消，0：已付款 }/{0:未核销,1:已核销,2:已结算}",name = "status")
    private Integer status;

    @ApiModelProperty(value = "商品名称",name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "代理人名称",name = "agentName")
    private String agentName;

    private String customerCode;
}
