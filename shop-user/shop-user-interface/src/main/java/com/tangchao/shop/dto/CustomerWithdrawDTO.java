package com.tangchao.shop.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerWithdrawDTO {
    private Long id;
    private Integer state;        //处理状态 0审核中  1审核通过  2审核不通过  3付款中  4已付款 5提现失败
    private Double withdrawPrice;    //提现金额
    private Long customerCode;        //申请人Id(主键id)
    private String userName;    //申请人(登录名)
    private String phone;//手机
    private String assessorId;        //审核人Id(主键id)
    private String assessorName;    //审核人(登录名)
    private Date applicationDate;        //申请时间
    private Date assessCompletionDate;    //审核时间
    private String withdrawUser;    //提现操作员
    private Date withdrawDate;    //提现操作时间
    private String applyReason;//审核不通过原因
    private String accountName;//账户名称(开户人)
    private String bank;//银行名称
    private String branchName;//开户支行名称
    private String bankId;//银行卡卡号
    private String inviteCode;//邀请码
    private String weixinName;//微信号
    private String alipayName;//支付宝账号
    private Integer payment;//支付方式{1：支付宝，2：微信}
    private String startApplicationDateStr;
    private String endApplicationDateStr;
    private String startAssessCompletionDateStr;
    private String endAssessCompletionDateStr;
    private String ids;
}
