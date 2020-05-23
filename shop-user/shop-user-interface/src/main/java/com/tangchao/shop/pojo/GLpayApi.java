package com.tangchao.shop.pojo;

import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/9 15:10
 */
@Data
public class GLpayApi {

    /**
     * GLpay生成的订单ID号
     */
    private String platform_trade_no;

    /**
     * 您的自定义订单号
     */
    private String orderid;

    /**
     * 订单定价
     */
    private String price;

    /**
     * 实际支付金额
     */
    private String realprice;

    /**
     * 您的自定义用户ID
     */
    private String orderuid;

    /**
     * 秘钥
     */
    private String key;
}
