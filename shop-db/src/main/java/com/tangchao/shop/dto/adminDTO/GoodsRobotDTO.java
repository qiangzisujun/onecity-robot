package com.tangchao.shop.dto.adminDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/30 18:17
 */
@Data
@ApiModel
public class GoodsRobotDTO {

    @ApiModelProperty(value = "id",name = "id")
    private Integer id;// 主键,自增

    @ApiModelProperty(value = "商品Id",name = "goodsId")
    private Long goodsId;// 商品Id

    @ApiModelProperty(value = "商品编号",name = "goodsNo")
    private String goodsNo;// 商品编号

    @ApiModelProperty(value = "总购买多少期",name = "periodsNumber")
    private Long periodsNumber;//总购买多少期

    @ApiModelProperty(value = "已经购买期数",name = "buyingPeriodsNumber")
    private Long buyingPeriodsNumber;//已经购买期数

    @ApiModelProperty(value = "每期购买百分比,小数显示",name = "percentage")
    private Double percentage;//每期购买百分比,小数显示

    @ApiModelProperty(value = "每期期商品机器人每次购买的范围值最大",name = "maxPurchasesCount")
    private Integer maxPurchasesCount;// 每期期商品机器人每次购买的范围值最大

    @ApiModelProperty(value = "每期商品机器人每次购买的范围值最小",name = "minPurchasesCount")
    private Integer minPurchasesCount;// 每期商品机器人每次购买的范围值最小

    @ApiModelProperty(value = "该期商品机器人每次购买的时间值最大",name = "maxPurchasesMinute")
    private Integer maxPurchasesMinute;// 该期商品机器人每次购买的时间值最大

    @ApiModelProperty(value = "该期商品机器人每次购买的时间值最小",name = "minPurchasesMinute")
    private Integer minPurchasesMinute;// 该期商品机器人每次购买的时间值最小

    @ApiModelProperty(value = "约定中奖者Id 不填：不约定 0：约定随机机器人中奖 正常值约定人中奖",name = "protocolLotteryNo")
    private String protocolLotteryNo;//约定中奖者Id 不填：不约定 0：约定随机机器人中奖 正常值约定人中奖

    @ApiModelProperty(value = "状态 0 不启用 1 启用",name = "status")
    private Integer status = 0 ;//状态 0 不启用 1 启用

    @ApiModelProperty(value = "lastModifyTime",name = "lastModifyTime")
    private Date lastModifyTime;//最后修改时间

    @ApiModelProperty(value = "商品名称",name = "goodsName")
    private String goodsName;//商品名称

    private Double jackPot;
}
