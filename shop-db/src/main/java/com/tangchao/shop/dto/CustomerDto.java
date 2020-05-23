package com.tangchao.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/23 15:20
 */
@Data
@ApiModel
public class CustomerDto {

    @ApiModelProperty(value = "页数",name = "pageNo")
    private Integer pageNo;

    @ApiModelProperty(value = "页数大小",name = "pageSize")
    private Integer pageSize;

    @ApiModelProperty(value = "开始日期",name = "startDate")
    private String startDate;

    @ApiModelProperty(value = "结束日期",name = "endDate")
    private String endDate;

    @ApiModelProperty(value = "用户手机号",name = "phone")
    private String userMobile;

    @ApiModelProperty(value = "订单状态(0:未核销,1:已核销,2:已清算)",name = "orderType")
    private String orderType;

    private Long userCode;
}
