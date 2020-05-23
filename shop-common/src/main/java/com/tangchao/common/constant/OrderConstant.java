package com.tangchao.common.constant;

/**
 * title: OrderConstant
 * package: com.chichao.eyyg.pojo.constant
 * description: 常用的订单状态
 * author: 王飞腾
 * date: 2018/7/26
 */
public abstract class OrderConstant {


    /**
     * 订单超时
     */
    public static final int TIME_OUT = -4;

    /**
     * 待确认订单
     */
    public static final int COMMIT = -3;

    /**
     * 未付款订单
     */
    public static final int UNPAID = -2;

    /**
     * 用户取消
     */
    public static final int USER_CANCEL = -1;

    /**
     * 已付款
     */
    public static final int ALREADY_PAID = 0;

    /**
     * 待发货
     */
    public static final int NOT_YET_SHIPPED = 1;

    /**
     * 已发货
     */
    public static final int ALREADY_SHIPPED = 2;

    /**
     * 确认收货
     */
    public static final int CONFIRM = 3;

    /**
     * 交易完成
     */
    public static final int END = 4;


    /**
     * 秒款订单（未秒款）
     */
    public static final int SECOND_MONEY = 5;

    /**
     * 已秒款订单
     */
    public static final int SECOND_SECTION = 6;


    /**
     * 待汇款 zhy 2019-08-29
     */
    public static final int NOT_YET_REMITTANCE = 7;

    /**
     * 已汇款 zhy 2019-08-29
     */
    public static final int ALREADY_REMITTANCE = 8;

}
