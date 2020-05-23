package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "winning_order")
@Data
public class WinningOrder implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  订单编号
    private String orderNo;

    //  订单状态{ 1：代发货，2：已发货，3：确认收货，4，交易完成 7.待汇款}
    private Integer orderStatus;

    //  商品编号
    private String goodsNo;

    //  期次Id
    private Long stageId;

    //  商品期次
    private Integer goodsStage;

    //  中奖人唯一标识
    private String customerCode;

    //  中奖人名称
    private String customerName;

    //  商品单价
    private Double goodsPrice;

    //  商品名称
    private String goodsName;

    //  商品图片
    private String goodsImg;

    //  商品厂商
    private String goodsFirm;

    //  商品规格
    private String goodsSpec;

    //  幸运号码
    private String openPrizeResult;

    //  中奖时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openPrizeTime;

    //  邮编
    private String zipCode;

    //  收货人名称
    private String userName;

    //  收件人地址
    private String userAddress;

    //  收件人手机
    private String userMobile;

    //  提交时间
    private Date submitTime;

    //  快递单号
    private String expressNo;

    //  是否为机器人订单 { 0：不是，1：是 }
    private Integer isRobot;

    //  快递公司
    private String expressCompany;

    //  订单备注
    private String orderRemarks;

    //  发货人Id
    private Long deliveryId;

    //  发货时间
    private Date deliveryTime;

    //  收货时间
    private Date takeGoodsTime;

    //  运费
    private Double expressCost;

    //  累计购买次数
    private Long buyNum;

    //  是否允许晒单｛0：不允许，1：允许｝
    private Integer isAllowSunburn;

    //  是否已晒单{ 2：未晒单，1：已晒单 }
    private Integer isShowOrder;

    // 中奖者累计购买总额
    @Transient
    private Double priceTotal;

    @Transient
    private String customerPhone;

    //  回收单价    zhy 19-08-29
    @Transient
    private Double recoveryPrice;

    @Transient
    private Integer isCustCode;//是否是指定中奖；0非指定，1指定

    @Transient
    private Integer blackStatus;//用户黑名单状态0 正常   1 已拉黑

}
