package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "shopping_cart")
@Data
public class ShoppingCart implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  会员Id
    private Long customerCode;

    //  商品编号
    private String goodsNo;

    //  期次Id
    private Long stageId;

    //  是否选中{ 0：未选中，1：选中 }
    private Integer isCheck;

    //  购买数量
    private Integer payNum;
}