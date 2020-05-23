package com.tangchao.shop.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderEnum {
    ORDER_DELETE(0, "删除订单已删除"),
    ORDER_DEAL_WITH(1, "未付款"),
    ORDER_PENDING_DELIVERY(2, "已付款,未发货"),
    ORDER_In_DISTRIBUTION(3, "已发货,未确认"),
    ORDER_COMPLETE(4, "交易成功"),
    ORDER_CANCEL(5, "交易关闭"),
    ORDER_SERVICE_USER(6, "已评价"),
    ;
    private int code;
    private String msg;
}
