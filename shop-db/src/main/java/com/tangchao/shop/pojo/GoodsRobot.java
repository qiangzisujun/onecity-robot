package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "goods_robot")
@Data
public class GoodsRobot implements Serializable {

    private static final long serialVersionUID = -3231294544565047952L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// 主键,自增

    private Long setId;// 设置Id

    private Long goodsId;// 商品Id

    private Long goodsPeriodId;// 商品期数Id

    private Integer buyRateMinute;// 购买率（分钟）

    private Integer goodsLimitCount;// 商品本身的限购次数

    private Integer maxCount;// 该期商品机器人能购买的最大数量

    private Integer boughtCount;// 该期商品机器人已购买的数量

    private Integer maxPurchasesConut;// 该期商品机器人每次购买的范围值最大

    private Integer minPurchasesConut;// 该期商品机器人每次购买的范围值最小

    private Integer maxPurchasesMinute;// 该期商品机器人每次购买的时间值最大

    private Integer minPurchasesMinute;// 该期商品机器人每次购买的时间值最小

    private Double jackPotNow;
    private Double jackPotAll;
    private Integer jackPotType;

    private String timestamp;
}
