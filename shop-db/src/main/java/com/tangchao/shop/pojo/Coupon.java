package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "coupon")
@Data
public class Coupon {

    @Id
    private Integer id;
    private String couponName;//优惠券名称
    private String img;//优惠券图片
    private String detailImg;//优惠券详情图
    private Integer efectiveTime;//有效时间（天）
    private BigDecimal couponAmount;//优惠券金额
    private Integer num;//发放数量（-1则表示无限，或者填写具体数量）
    private BigDecimal purchaseAmount;//优惠券购买金额
    private Integer luckdrawNum;//获得抽奖次数
    private Integer sort;//排序 数字越小越靠前
    private String description;//使用说明
    private Integer isShare;//是否分享好友（0是，1否）
    private Integer status; //是否删除（0，删除，1正常）
    private Date createTime;//创建时间
}
