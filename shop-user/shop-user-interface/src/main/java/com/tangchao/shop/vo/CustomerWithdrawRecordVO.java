package com.tangchao.shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CustomerWithdrawRecordVO implements Serializable {

    @ApiModelProperty(value = "id", name = "id")
    private Long id;

    @ApiModelProperty(value = "手续费", name = "serviceChargeAmount")
    private Double serviceChargeAmount;

    @ApiModelProperty(value = "申请日期", name = "applicationDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applicationDate;

    @ApiModelProperty(value = "提现金额", name = "withdrawPrice")
    private Double withdrawPrice;

    private Integer payment;
    private Integer state;

    @ApiModelProperty(value = "提现方式", name = "paymentName")
    private String paymentName;

    @ApiModelProperty(value = "审核状态", name = "stateName")
    private String stateName;

    public String getPaymentName() {
        switch (this.payment) {
            case 1:
                this.paymentName = "银行";
                break;
            case 2:
                this.paymentName = "微信";
                break;
            case 3:
                this.paymentName = "支付宝";
                break;
            default:
                this.paymentName = "";
                break;
        }
        return paymentName;
    }

    public String getStateName() {
        switch (this.state) {
            case 1:
                this.stateName = "审核通过";
                break;
            case 2:
                this.stateName = "审核不通过";
                break;
            case 3:
                this.stateName = "已提现";
                break;
            default:
                this.stateName = "审核中";
                break;
        }
        return stateName;
    }


}
