package com.tangchao.shop.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Class ShopReturnOrder
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/21 14:15
 * @Version 1.0
 **/
@Data
@Table(name = "shop_return_order")
public class ShopReturnOrder {

    @Id
    @KeySql(useGeneratedKeys = true)
    private String id;
    private String orderId;
    private String orderNo;
    private Long actualPay;// 实付金额
    private Long userCode;
    private String description;// 买家留言
    private String replyMessage;// 买家留言
    private String images;
    private Integer status;
    private Long totalIntegral;
    private String platformOrderNo;
    private String userName;// 买家昵称
    private String userMobile;
    private Date createTime;
    private Date updateTime;
    private Integer datalevel;

}
