package com.tangchao.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class GoodsLotteryVO {

    @ApiModelProperty(value = "用户名", name = "userName")
    private String userName;

    @ApiModelProperty(value = "用户头像", name = "userIcon")
    private String userIcon;

    @ApiModelProperty(value = "用户标识", name = "userCode")
    private String userCode;

    @ApiModelProperty(value = "抽奖码", name = "resultCode")
    private String lotteryCode;

    @ApiModelProperty(value = "中奖幸运号码", name = "resultCode")
    private Long resultCode;

    @ApiModelProperty(value = "商品期数id", name = "id")
    private Long id;

    @ApiModelProperty(value = "商品图片", name = "goodsPicture")
    private String goodsPicture;

    @ApiModelProperty(value = "活动标记 {1：活动商品，2：非活动商品 }", name = "isActivity")
    private Integer isActivity;

    @ApiModelProperty(value = "满团时间", name = "fullTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fullTime;

    @ApiModelProperty(value = "商品期数", name = "stageId")
    private Long stageId;

    @ApiModelProperty(value = "是否开奖｛0：未开奖，1：已开奖，2：开奖中｝", name = "isAward")
    private Integer isAward;

    @ApiModelProperty(value = "商品编号", name = "goodsNo")
    private String goodsNo;

    @ApiModelProperty(value = "购买时间", name = "buyTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date buyTime;

    @ApiModelProperty(value = "开奖时间", name = "openTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openTime;

    @ApiModelProperty(value = "购买次数", name = "count")
    private Integer count;

    @ApiModelProperty(value = "最大期数", name = "maxStageIndex")
    private Integer maxStageIndex;

    @ApiModelProperty(value = "是否显示", name = "isShow")
    private Integer isShow;

    @ApiModelProperty(value = "下单时间", name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
