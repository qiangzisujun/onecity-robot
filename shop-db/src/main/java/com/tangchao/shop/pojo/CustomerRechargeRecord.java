package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "customer_recharge_record")
@Data
public class CustomerRechargeRecord {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 代理会员编码
    private Long puserCode;

    //会员编码
    private Long customerCode;

    private Double amount;

    //充值消费积分
    private Double integral;

    //订单编号
    private String rechargeCode;

    //描述
    private String rechargeDescribe;

    //充值消费标识{ 1：充值，2：消费 }
    private Integer type;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //创建人ID
    private Long createId;

    //支付方式{ 1：支付宝，2：微信，3：余额，4：后台，5：代理 }
    private Integer payment;

    private Double deductionsScore; //扣减积分
    @Transient
    private String userName;
    @Transient
    private String userMobile;//会员手机号

    @Transient
    private String agentName;//代理人名称

    @Transient
    private String agentMobile;//代理人手机号

    @Transient
    private String managerName;//会员名称

    @Transient
    private Double integralSum;


    @Transient
    private String blackStatu; // 黑名单状态（0 正常   1 已拉黑）
    @Transient
    private String whiteStatu; // 白名单状态 （0 正常   1 白名单用户）
    @Transient
    private Double winningTotal; // 中奖金额

    @Transient
    private Double payAmountSum;//消费总额

    private Integer goodsTypeId;



    @Transient
    private String paymentStr;

    public String getPaymentStr() {
        if (this.payment!=null){
            switch (this.payment){
                case 1:
                    this.paymentStr="支付宝";
                    break;
                case 2:
                    this.paymentStr="微信";
                    break;
                case 3:
                    this.paymentStr="余额";
                    break;
                case 4:
                    this.paymentStr="后台";
                    break;
                case 5:
                    this.paymentStr="代理";
                    break;
                case 6:
                    this.paymentStr="银行";
                    break;
                case 7:
                    this.paymentStr="佣金";
                    break;
                case 8:
                    this.paymentStr="虚拟卡充值";
                    break;
                case 9:
                    this.paymentStr="不中全返";
                    break;
                case 10:
                    this.paymentStr="支付猫";
                    break;
                default:
                    this.paymentStr="";
                    break;
            }
        }else{
            this.paymentStr="";
        }
        return paymentStr;
    }

}
