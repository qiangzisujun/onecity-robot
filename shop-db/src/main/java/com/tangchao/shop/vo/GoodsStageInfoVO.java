package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class GoodsStageInfoVO {

    @ApiModelProperty(value = "商品期数Id", name = "id")
    private Long id;

    @ApiModelProperty(value = " 标识｛0：正常，-1：删除｝", name = "flag")
    private Integer flag;

    @ApiModelProperty(value = " 上架时间", name = "sellStartTime")
    private Date sellStartTime;

    @ApiModelProperty(value = " 商品热度", name = "goodsHot")
    private Integer goodsHot;

    @ApiModelProperty(value = " 商品期数", name = "stageIndex")
    private Integer stageIndex;

    @ApiModelProperty(value = " 商品id（goods_info）", name = "stageIndex")
    private Long goodsId;

    @ApiModelProperty(value = "商品类别（goods_type）", name = "typeId")
    private Long typeId;

    @ApiModelProperty(value = "商品唯一编码", name = "goodsNo")
    private String goodsNo;

    @ApiModelProperty(value = "商品名称", name = "goodsName")
    private String goodsName;

    @ApiModelProperty(value = "商品品牌", name = "goodsBrand")
    private String goodsBrand;

    @ApiModelProperty(value = "商品规格", name = "goodsSpec")
    private String goodsSpec;

    @ApiModelProperty(value = "商品库存", name = "goodsInv")
    private Integer goodsInv;

    @ApiModelProperty(value = "商品价格", name = "goodsInv")
    private Double goodsPrice;

    @ApiModelProperty(value = "商品图片url", name = "goodsPicture")
    private String goodsPicture;

    @ApiModelProperty(value = "商品详情图片url", name = "goodsInfoPicture")
    private String goodsInfoPicture;

    @ApiModelProperty(value = "商品编码", name = "goodsCode")
    private String goodsCode;

    @ApiModelProperty(value = "是否允许晒单｛0：不允许，1：允许｝", name = "isShowOrder")
    private Integer isShowOrder;

    @ApiModelProperty(value = "回收价格", name = "recoveryPrice")
    private Double recoveryPrice;

    @ApiModelProperty(value = "每人限购次数", name = "goodsCode")
    private Integer buyNum;

    @ApiModelProperty(value = "每次购买价格", name = "buyPrice")
    private Double buyPrice;

    @ApiModelProperty(value = "购买总数", name = "buySize")
    private Integer buySize;

    @ApiModelProperty(value = "当前购买数", name = "buyIndex")
    private Integer buyIndex;

    @ApiModelProperty(value = "是否开奖｛0：未开奖，1：已开奖，2：开奖中｝", name = "isAward")
    private Integer isAward;

    @ApiModelProperty(value = "满团时间", name = "fullTime")
    private Date fullTime;

    @ApiModelProperty(value = "开奖结果", name = "awardResults")
    private String awardResults;

    @ApiModelProperty(value = "开奖时间", name = "openTime")
    private Date openTime;

    @ApiModelProperty(value = "活动标记  {1：活动商品，2：非活动商品 }", name = "isActivity")
    private Integer isActivity;

    //	活动Id
    @ApiModelProperty(value = "活动Id", name = "activityId")
    private Long activityId;

    @ApiModelProperty(value = "指定中奖用户编号", name = "custCode")
    private String custCode;

    @ApiModelProperty(value = "最大商品期数", name = "maxStageIndex")
    private Integer maxStageIndex;

    @ApiModelProperty(value = "倒计时毫秒值", name = "calcTimeMsec")
    private Long calcTimeMsec;

    @ApiModelProperty(value = "是否开启流量赠送功能", name = "isFlow")
    private Boolean isFlow;

    @ApiModelProperty(value = "可购买数量", name = "hasBuy")
    private Long hasBuy;


    @ApiModelProperty(value = "商品是否收藏(0:不是,1是)", name = "isCollection")
    private Integer isCollection;
}
