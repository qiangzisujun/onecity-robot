package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Class UserCoupon
 * @Description TODO
 * @Author Aquan
 * @Date 2020.2.17 17:24
 * @Version 1.0
 **/
@Data
@Table(name = "user_coupon")
public class UserCoupon {

    @Id
    private Integer id;//主键自增id',
    private Integer couponId;//优惠券ID
    private String couponName;//优惠券名称
    private String couponCode;//优惠券抵消码Code
    private String img;//优惠券图片
    private Date efectiveTime;//有效时间至
    private BigDecimal couponAmount;//优惠券可抵扣金额
    private BigDecimal purchaseAmount;//优惠券购买金额
    private String description;//使用说明
    private Integer isShare;//是否分享好友（0是，1否）'
    private Integer couponStatus; //状态 0/未使用 1/已使用'
    private Integer datalevel; //是否删除（0，删除，1正常）
    private Date createTime;//创建时间
    private Date updateTime;//'更新时间'
    private String userCode;//用户Code
    private String couponLogNo;//购买记录流水号
}
