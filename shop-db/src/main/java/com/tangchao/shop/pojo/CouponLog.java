package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Class CouponLog
 * @Description TODO
 * @Author Aquan
 * @Date 2020.2.15 18:25
 * @Version 1.0
 **/
@Data
@Table(name = "coupon_log")
public class CouponLog {

    @Id
    private Integer id;
    private Long userCode;//'用户ID'
    private Integer couponId;//'优惠券ID'
    private BigDecimal payAmount;//'付款余额'
    private Integer logStatus; //状态（0未付款，1已支付）
    private Integer datalevel; //是否删除（0，删除，1正常）
    private Date createTime;//创建时间
    private Date updateTime;//'更新时间'
    private String no;//流水号
    private Integer number;//数量

}
