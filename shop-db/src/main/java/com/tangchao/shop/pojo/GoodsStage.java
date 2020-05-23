package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "goods_stage")
@Data
public class GoodsStage implements Serializable {

    // 主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 标识｛0：正常，-1：删除｝
    private Integer flag;

    // 创建者
    private Long createId;

    // 创建时间
    private Date createTime;

    // 修改者
    private Long lastModifyId;

    // 更新时间
    private Date lastModifyTime;

    // 上架时间
    private Date sellStartTime;

    // 商品热度
    private Integer goodsHot;

    // 商品期数
    private Integer stageIndex;

    // 商品id（goods_info）
    private Long goodsId;

    // 商品类别（goods_type）
    private Long typeId;

    // 商品唯一编码
    private String goodsNo;

    // 商品名称
    private String goodsName;

    // 商品品牌
    private String goodsBrand;

    // 商品规格
    private String goodsSpec;

    // 商品库存
    private Integer goodsInv;

    // 商品价格
    private Double goodsPrice;

    // 商品图片url
    private String goodsPicture;

    // 商品详情图片url
    private String goodsInfoPicture;

    // 商品编码
    private String goodsCode;

    // 是否允许晒单｛0：不允许，1：允许｝
    private Integer isShowOrder;

    // 回收价格
    private Double recoveryPrice;

    // 每人限购次数
    private Integer buyNum;

    // 每次购买价格
    private Double buyPrice;

    // 购买总数
    private Integer buySize;

    // 当前购买数
    private Integer buyIndex;

    // 是否开奖｛0：未开奖，1：已开奖，2：开奖中｝
    private Integer isAward;

    // 满团时间
    private Date fullTime;

    // 开奖结果
    private String awardResults;

    // 开奖时间
    private Date openTime;

    //	活动标记  {1：活动商品，2：非活动商品 }
    private Integer isActivity;

    //	活动Id
    private Long activityId;

    private String custCode;

    /* -------------------------------------------------------------------------------------------------------------- */

    // 最大商品期数
    @Transient
    private Integer maxStageIndex;

    // 倒计时毫秒值
    @Transient
    private Long calcTimeMsec;

    /* -------------------------------------------------------------------------------------------------------------- */

    private Double jackPotNow;
    private Double jackPotAll;
    private Integer jackPotType;
}
