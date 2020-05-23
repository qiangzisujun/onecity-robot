package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "goods_favourite")
@Data
public class GoodsFavourite implements Serializable {
    // 主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerCode;//'会员唯一标识'
    private Long goodsStageId;//'商品期数表主键Id'
    private Date createTime;//'创建时间'
    private String goodsNo;//商品唯一编码

    @Transient
    private String goodsPicture;// 商品图片url

    @Transient
    private Double goodsPrice;// 商品价格

    @Transient
    private Integer goodsInv;// 剩余数量

    @Transient
    private Integer buySize;// 总需数量

    @Transient
    private Integer buyIndex;// 正在参与

    @Transient
    private  String goodsName;//商品名称

}
