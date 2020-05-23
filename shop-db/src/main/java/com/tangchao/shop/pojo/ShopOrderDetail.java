package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "shop_order_detail")
@Data
public class ShopOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;// 订单id

    private Long goodsId;// 商品id

    private Integer num;// 商品购买数量

    private String title;// 商品标题

    private Long price;// 商品单价
    private String image;// 图片
    private Integer status;
    private Date createTime;
    private Date paymentTime;
    private Date endTime;
    private Date closeTime;
    private Date commentTime;
    private Long specificationsId;
    private String specificationsName;
    private Integer datalevel;
    private Long integral;
}
