package com.tangchao.common.constant;

/**
 * title: PayStatusConstant
 * package: com.chichao.eyyg.pojo.constant
 * description: 常用的支付状态标识
 * author: 王飞腾
 * date: 2018/7/23
 */
public abstract class PayStatusConstant {

    /**
     * 收入
     */
    public static final Integer INCOME = 1;

    /**
     * 支出
     */
    public static final Integer EXPENDITURE = 2;

    //  支付方式
    /* --------------------------------------------------------------- */

    /**
     * 支付宝
     */
    public static final Integer PAY_FROM_ALIPAY = 1;

    /**
     * 微信
     */
    public static final Integer PAY_FROM_WECHAT = 2;

    /**
     * 余额
     */
    public static final Integer PAY_FROM_MONEY = 3;

    /**
     * 后台
     */
    public static final Integer PAY_FROM_BACKSTAGE = 4;

    /**
     * 代理
     */
    public static final Integer PAY_FROM_PROXY = 5;

    /**
     * 商城卡
     */
    public static final Integer SHOPPING_CARD = 6;

    /**
     * 银行
     */
    public static final Integer PAY_FROM_BANK = 6;

    /**
     * 提现
     */
    public static final Integer PAY_FROM_WITHDRAW = 7;

    /**
     * 虚拟卡充值
     */
    public static final Integer PAY_FROM_VIRTUAL = 8;

    /**
     * 不中全返
     */
    public static final Integer PAY_FROM_BACK = 9;

    /**
     * 支付猫
     */
    public static final Integer PAY_FROM_BACK_9MAO = 10;


    /**
     * 后台充值赠送
     */
    public static final Integer PAY_ADMIN_HANDSELMONEY = 11;

}
