package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "goods_robot_set")
@Data
public class GoodsRobotSet implements Serializable {

    private static final long serialVersionUID = -3101632855174444211L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// 主键,自增

    private Long goodsId;// 商品Id

    private String goodsNo;// 商品编号

    private Long periodsNumber;//总购买多少期

    private Long buyingPeriodsNumber;//已经购买期数

    private Double percentage;//每期购买百分比,小数显示

    private Integer maxPurchasesCount;// 每期期商品机器人每次购买的范围值最大

    private Integer minPurchasesCount;// 每期商品机器人每次购买的范围值最小

    private Integer maxPurchasesMinute;// 该期商品机器人每次购买的时间值最大

    private Integer minPurchasesMinute;// 该期商品机器人每次购买的时间值最小

    private String protocolLotteryNo;//约定中奖者Id 不填：不约定 0：约定随机机器人中奖 正常值约定人中奖

    private Integer status;//状态 0 不启用 1 启用

    private Long createId;//创建人Id

    private Date createTime;//创建时间

    private Long lastModifyId;//最后修改人Id

    private Date lastModifyTime;//最后修改时间

    private String goodsName;//商品名称

    private Double jackPot;

}
