package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

@Table(name = "goods_info")
@Data
public class Goods implements Serializable {

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

    // 在售期数
    private Integer sellStage;

    // 是否在售｛0：下架，1：上架｝
    private Integer isSell;

    // 上架时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sellStartTime;

    // 下架时间
    private Date sellEndTime;

    // 是否推荐商品{0：否，1：推荐商品}
    private Integer isRcmd;

    // 是否新品{0：否，1：新品}
    private Integer isNew;

    // 活动标记 {1：活动商品，2：非活动商品 }
    private Integer isActivity;

    // 活动购买次数
    private Integer activityBuyNum;

    @Transient
    private int goodsHot;

    @Transient
    private String typeName;
}
