package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/10 21:10
 */
@Table(name = "payment_order_platform")
@Data
public class PaymentOrderPlatform {

    private Long id;
    private Integer paymentType;//支付类型(1：支付宝；2：微信支付)
    private Double paymentMoney;//支付金额
    private Date paymentTime;//支付时间
    private Integer paymentStatus;//支付状态（1未支付，2，支付成功，3支付失败）
    private String paymentUserCode;//支付人code
    private Integer flag;//记录状态
    private String paymentOrderNo;//支付订单号
    private String platformTradeNo;//平台交易订单号
    private String platformType;//平台类型

}
