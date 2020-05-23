package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.Date;


@Table(name = "order_distribution")
@Data
public class OrderDistribution implements Serializable {

    //  Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  订单编号
    private String orderNo;

    //  订单总额
    private Double orderTotal;

    //  提点比例
    private Double remindSpec;

    //  提点金额
    private Double remindMoney;

    //  提点层级
    private Integer remindLayer;

    //  订单消费者编号
    private Long purchaserCode;

    //  受益者编号
    private Long beneficiaryCode;

    //  创建时间
    private Date createTime;
    
    @Transient
    private String userName;//线下人员

}
