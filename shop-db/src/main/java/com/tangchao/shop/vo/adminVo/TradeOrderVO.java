package com.tangchao.shop.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tangchao.shop.pojo.OrderGoods;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/30 10:55
 */
@Data
public class TradeOrderVO {
    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  订单编号
    private String orderNo;

    //  采购者唯一标识
    private String purchaseCode;

    //  采购者名称
    private String purchaseName;

    //  订单状态{ -4:订单超时，-3:待确认订单，-2：未付款订单，-1：用户取消，0：已付款 }
    private Integer orderStatus;

    //  商品总额,未进行任何折扣的总价格
    private Double goodsTotal;

    //  订单总额,进行各种折扣之后的金额    订单总额 = ( 商品总额 - 积分抵扣金额 - ... );
    private Double orderTotal;

    //  支付来源{ 0：余额，1：支付宝，2：微信，3：余额 }
    private Integer payFrom;

    //  邮编
    private String zipCode;

    //  收货人名称
    private String userName;

    //  收件人地址
    private String userAddress;

    //  收件人手机
    private String userMobile;

    //  消费积分
    private Long consumeScore;

    //  积分抵扣金额
    private Double scoreDeductionMoney;

    //  订单赠送积分
    private Double orderScore;

    //  会员删除标记 { 0：正常,1：已删除 }
    private Integer userDelFlag;

    //  删除标记 { 0：正常,1：已删除 }
    private Integer delFlag;

    //  是否为机器人订单 { 0：不是，1：是 }
    private Integer isRobot;

    //  创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //  下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date downOrderTime;

    //  支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    //  订单备注
    private String orderRemarks;

    //  是否自动购买下一期{ 1：是,其他：不是 }
    private Integer isAutoBuyNext;

    //  购买Ip
    private String payIp;

    // 最小支付金额
    private Double minPayMoney;

    private Integer isActivity;

    //  订单商品列表
    @Transient
    private List<OrderGoods> orderGoodsList = new ArrayList<>(0);

    @Transient
    private String orderStatusStr;//订单状态字符串

    @Transient
    private String phone;

    @Transient
    private Integer blackStatus;//用户黑名单状态0 正常   1 已拉黑

    @Transient
    private Double winningTotal; // 中奖金额

    @Transient
    private Double payAmountSum;//消费总额

    public String getOrderStatusStr() {

        if (this.orderStatus!=null){
            switch (this.orderStatus) {
                case -4:
                    this.orderStatusStr = "订单超时";
                    break;
                case -3:
                    this.orderStatusStr = "待确认订单";
                    break;
                case -2:
                    this.orderStatusStr = "未付款订单";
                    break;
                case -1:
                    this.orderStatusStr = "用户取消";
                    break;
                case 0:
                    this.orderStatusStr = "已付款";
                    break;
                default:
                    this.orderStatusStr = "";
                    break;
            }
        }
        return orderStatusStr;
    }

}
