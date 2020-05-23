package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/8 17:25
 */
@Table(name = "express_form")
@Data
public class ExpressForm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  是否显示订单号{ 0：显示，1：不显示 }
    private Integer orderNo;

    //  是否显示快递单号{ 0：显示，1：不显示 }
    private Integer expressNo;

    //  是否显示快递公司{ 0：显示，1：不显示 }
    private Integer expressCompany;

    //  是否显示快递发货时间{ 0：显示，1：不显示 }
    private Integer consignorTime;

    //  是否显示收货人名称{ 0：显示，1：不显示 }
    private Integer userName;

    //  是否显示收货人手机{ 0：显示，1：不显示 }
    private Integer userMobile;

    //  是否显示收货人地址{ 0：显示，1：不显示 }
    private Integer userAddress;

    //  是否显示收货人订单备注{ 0：显示，1：不显示 }
    private Integer orderRemarks;

    public ExpressForm(){

    }

    public ExpressForm(Integer initNum) {
        this.orderNo = initNum;
        this.expressNo = initNum;
        this.expressCompany = initNum;
        this.consignorTime = initNum;
        this.userName = initNum;
        this.userMobile = initNum;
        this.userAddress = initNum;
        this.orderRemarks = initNum;
    }
}
