package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "cms_payment_record")
@Data
public class PaymentRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 平台手机号
    private String userMobile;

    // 平台头像
    private String headPortrait;

    // 平台昵称
    private String realname;

    // 微信头像
    private String wechatHeadPortrait;

    // 微信昵称
    private String wechatRealname;

    // 收款码类型（1.微信  2.支付宝   默认值为1）
    private String paymentCodeType;

    // 收款码金额
    private Double paymentCodePrice;

    // 收款码图片
    private String paymentCodeImage;

    // 用户唯一标识
    private String customerCode;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String rechargeStatu;

    private String flag;

    @Transient
    private String userMoney;

}
