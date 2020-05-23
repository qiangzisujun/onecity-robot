package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Table(name = "customer_employ_tixian_record")
@Data
public class CustomerEmployTiXianRecord {

    private static final long serialVersionUID = -7559263867722790817L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer state;        //'处理状态 0审核中  1审核通过  2审核不通过  3已提现 '
    private Double withdrawPrice;    //提现金额
    private String customerCode;        //申请人Id(主键id)
    private String userName;    //申请人(登录名)
    private String phone;//手机
    private String assessorId;        //审核人Id(主键id)
    private String assessorName;    //审核人(登录名)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applicationDate;        //申请时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assessCompletionDate;    //审核时间
    private String withdrawUser;    //提现操作员
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date withdrawDate;    //提现操作时间
    private String applyReason;//审核不通过原因
    private String accountName;//账户名称(开户人)
    private String bank;//银行名称
    private String branchName;//开户支行名称
    private String bankId;//银行卡卡号
    private Double remainder;//余额（会员剩余可提）
    private Double serviceChargeAmount;//手续费
    private String weixinName;//微信号
    private String alipayName;//支付宝账号
    private Integer payment;//支付方式{1：银行，2：微信，3：支付宝}

    @Transient
    private Double employMoneySum;//佣金总额
    @Transient
    private Double checkEmployMoneySum;//正在审核的佣金总额
    @Transient
    private Double arEmployMoneySum;//已提现的佣金总额
    @Transient
    private Integer friendSum; //邀请的好友总数

    private String paymentCodeImg;//收款码图片
}
