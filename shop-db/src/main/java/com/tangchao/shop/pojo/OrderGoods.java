package com.tangchao.shop.pojo;


import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(name = "order_goods")
@Data
public class OrderGoods implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //商品编号+商品其次
    //private String tag;

    //  订单编号
    private String orderNo;

    //  商品编号
    private String goodsNo;

    //  期次Id
    private Long stageId;

    //  商品期次
    private Integer goodsStage;

    //  购买单价
    private Double payPrice;

    //  商品单价
    private Double goodsPrice;

    //  商品价格合计
    private Double goodsTotal;

    //  商品名称
    private String goodsName;

    //  商品图片
    private String goodsImg;

    //  商品厂商
    private String goodsFirm;

    //  商品规格
    private String goodsSpec;

    //  购买数量
    private Integer payNum;

    //  是否允许晒单｛0：不允许，1：允许｝
    private Integer isAllowSunburn;

    //  当前购买数
    @Transient
    private Integer buyIndex;

    //  支付Ip
    @Transient
    private String payIp;

    // 购买总数
    @Transient
    private Integer buySize;

    @Transient
    private Integer isAward;

    @Transient
    private String custCode;

    @Transient
    private String lastModifyName;  // 修改者

    //  抽奖信息
    @Transient
    private List<Lottery> lotteryList = new ArrayList<>(0);

    @Transient
    private Integer isDesignation;//是否指定中奖;1:显示，2显示取消

    @Transient
    private Long userCode;
}
