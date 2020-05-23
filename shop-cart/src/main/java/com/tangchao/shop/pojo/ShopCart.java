package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "shop_cart")
@Data
public class ShopCart {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userCode;// 会员唯一标识,外部用户,同步
    private Long goodsId;// 商品id
    private String goodsName;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer number;// 购买数量
    private Long specifications;
    private Date addTime;
    private Integer status;
    private Long integral;

}
