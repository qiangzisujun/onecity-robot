package com.tangchao.shop.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "shop_order")
@Data
public class ShopOrder {

    @Id
    @KeySql(useGeneratedKeys = true)
    private String orderId;// id
    private String orderNo;
    private Long totalPay;// 总金额
    private Long actualPay;// 实付金额
    private Integer paymentType;
    private Date createTime;// 创建时间
    private Long userCode;
    private String buyerMessage;// 买家留言
    private String buyerNick;// 买家昵称
    private String receiverAddress;
    private Integer status;
    private Date paymentTime;
    private Boolean buyerRate;// 买家是否已经评价
    private String userName;
    private String userMobile;
    private String zipCode;
    private Integer datalevel;
    private String trackingNumber;
    private String courierCompany;
    private Long totalIntegral;
    private Long specId;//商品规格Id
    private String specName;//属性规格名称
    private String platformOrderNo;
    private Integer orderType;
    private String virtualAccount;
    private String virtualMessage;

    @Transient
    private ShopOrderDetail shopOrderDetail;

    @Transient
    private String phone;
}