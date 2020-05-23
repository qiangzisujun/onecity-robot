package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "customer_info")
@Data
public class CustomerInfo {

    //  用户Id，主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  会员唯一标识
    private Long customerCode;

    //  账户余额
    private Double userMoney;

    //  会员流量
    private Double userFlow;

    //  会员积分
    private Double userScore;

    //  注册Ip
    private String registerIp;

    //  邀请码
    private String inviteCode;

    //  创建人Id
    private Long createId;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

    //佣金余额
    private Double employMoney;

    private Double registerScore;
    private Double registerMoney;
}
