package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "goods_locking")
@Data
public class GoodsLocking implements Serializable {

    // 主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer flag; //标识 0：正常 -1：删除

    private Long userCode; //用户编码

    private Long stageId; //商品期次id

    private Long orderNo; //订单编号

    private Long goodsNo; //商品编号

    private Integer num; //锁定数量

    private Date createTime; //锁定时间

    private Date updateTime; //修改时间
}
