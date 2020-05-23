package com.tangchao.shop.dto;

import lombok.Data;

@Data
public class TradeOrderDTO {

    // 会员编号
    private Long customerCode;

    // 订单状态{ -2：未付款订单，-1：用户取消，0：已付款，1：代发货，2：已发货，3：确认收货，4，交易完成 }
    private Integer orderStatus;

    // 是否使用积分抵扣{ 1：使用，2：不使用 }
    private Integer isUseIntegral;

    // 订单编号
    private Long orderNo;

    // 邮编
    private String zipCode;

    // 收货人名称
    private String userName;

    // 收件人地址
    private String userAddress;

    // 收件人手机
    private String userMobile;

    // 商品期次Id列表
    private Long stageId;

    // 购买数量
    private Integer payNum;

    // 订单备注
    private String orderRemarks;

    // 开奖状态{ null：全部,0：进行中，其他：已揭晓 }
    private Integer openWinningStatus;

    // 是否自动购买下一期{ 1：是,其他：不是 }
    private Integer isAutoBuyNext;

    // 购买Ip
    private String payIp;

    // 开始时间
    private String beginDate;

    // 结束时间
    private String endDate;

    // 电话
    private String phone;

    // 用户名
    private String customerName;

    // 商品编码
    private String goodsNo;

    private String ids;

    private boolean isAutoBy = true;

    //  是否为机器人{ 0：不是，1：是 }
    private Integer isRobot;
}
