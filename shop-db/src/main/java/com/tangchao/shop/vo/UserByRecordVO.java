package com.tangchao.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class UserByRecordVO {

    @ApiModelProperty(value = "获奖者", name = "winnersName")
    private String winnersName;

    @ApiModelProperty(value = "商品编号", name = "goodsNo")
    private String goodsNo;

    @ApiModelProperty(value = "商品图片", name = "goodsImg")
    private String goodsImg;

    @ApiModelProperty(value = "商品期数", name = "goodsStage")
    private String goodsStage;

    @ApiModelProperty(value = "商品名称", name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "获奖者标识", name = "resultUserCode")
    private String resultUserCode;

    @ApiModelProperty(value = "揭晓时间", name = "openWinningTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openWinningTime;

    @ApiModelProperty(value = "当前购买数", name = "buyIndex")
    private Integer buyIndex;

    @ApiModelProperty(value = "购买总数", name = "buySize")
    private Integer buySize;

    @ApiModelProperty(value = "剩余数量", name = "goodsInv")
    private Integer goodsInv;

    @ApiModelProperty(value = "购买时间", name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "开奖码", name = "createTime")
    private String lotteryCode;

    @ApiModelProperty(value = "是否中奖{ 0：未开奖，1：未中奖，2：中奖 }", name = "isWinning")
    private Integer isWinning;

    @ApiModelProperty(value = "商品价格", name = "goodsPrice")
    private Double goodsPrice;
}
