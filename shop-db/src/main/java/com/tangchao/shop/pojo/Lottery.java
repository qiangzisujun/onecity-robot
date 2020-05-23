package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "lottery")
@Data
public class Lottery implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  订单商品Id
    private Long orderGoodsId;

    //  商品唯一编码
    private String goodsNo;

    //  期次Id
    private Long stageId;

    //  商品期次
    private Integer goodsStage;

    //  会员唯一标识
    private Long customerCode;

    //  幸运号码
    private String lotteryCode;

    //  创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //  是否为机器人{ 0：不是，1：是 }
    private Integer isRobot;

    //  开奖时间
    private Date openWinningTime;

    //  是否中奖{ 0：未开奖，1：未中奖，2：中奖 }
    private Integer isWinning;

    //  中奖幸运号码
    private String resultCode;

    //  中奖人唯一标识
    private Long resultUserCode;

    //  购买Ip
    private String payIp;

    //  购买人名称
    @Transient
    private String userName;

    @Transient
    private Long userCode;

    //买入次数
    @Transient
    private Integer buying ;

}