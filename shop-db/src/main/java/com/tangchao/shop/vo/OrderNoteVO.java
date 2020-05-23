package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.util.Date;

@Data
@ApiModel
public class OrderNoteVO {

    @ApiModelProperty(value = "商品Id", name = "goodsId")
    private String goodsId;

    @ApiModelProperty(value = "用户名", name = "winnersName")
    private String winnersName;

    @ApiModelProperty(value = "商品期次", name = "goodsStage")
    private String goodsStage;

    @ApiModelProperty(value = "抽奖码", name = "count")
    private String count;

    @ApiModelProperty(value = "幸运号码", name = "lotteryCode")
    private String lotteryCode;

    @ApiModelProperty(value = "是否中奖", name = "isWinning")
    private String isWinning;

    @ApiModelProperty(value = "商品名称", name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "商品图片", name = "goodsImg")
    private String goodsImg;

    @ApiModelProperty(value = "总需", name = "buySize")
    private int buySize;

    @ApiModelProperty(value = "已参与", name = "buyIndex")
    private int buyIndex;

    @ApiModelProperty(value = "剩余", name = "goodsInv")
    private int goodsInv;

    @ApiModelProperty(value = "商品唯一编码", name = "goodsInv")
    private String goodsNo;


    @ApiModelProperty(value = "活动标记", name = "isActivity")
    @Transient
    private Integer isActivity;

    @ApiModelProperty(value = "购买时间", name = "createTime")
    private Date createTime;

    @ApiModelProperty(value = "开奖时间", name = "openWinningTime")
    private Date openWinningTime;

    @ApiModelProperty(value = "开奖号", name = "resultUserCode")
    private String resultUserCode;
}
