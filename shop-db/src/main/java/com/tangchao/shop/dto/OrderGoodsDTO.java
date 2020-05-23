package com.tangchao.shop.dto;

import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/17 16:42
 */
@Data
public class OrderGoodsDTO {

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
    private Integer buyIndex;

    private String payIp;

    private Integer buySize;

    private Integer isAward;

    private String custCode;

    private String lastModifyName;  // 修改者

    private Integer isDesignation;//是否指定中奖;1:显示，2显示取消

    private Integer isRobot;
}
