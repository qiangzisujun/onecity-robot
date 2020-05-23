package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/23 14:08
 */
@Data
@Table(name = "customer_purchases")
public class CustomerPurchases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userCode;
    private String goodsNo;
    private Integer num;

}
