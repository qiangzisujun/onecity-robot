package com.tangchao.shop.dto.adminDTO;

import com.tangchao.shop.pojo.ShopOrderDetail;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Class SysShopOrderDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/10 17:44
 * @Version 1.0
 **/
@Data
public class SysShopOrderDTO {

    private String orderId;// id
    private String orderNo;
    private Long totalPay;// 总金额
    private Long actualPay;// 实付金额
    private Date createTime;// 创建时间
    private String buyerMessage;// 买家留言
    private String buyerNick;// 买家昵称
    private String receiverAddress;
    private Integer status;
    private Date paymentTime;
    private Boolean buyerRate;// 买家是否已经评价
    private String userName;
    private String userMobile;
    private String zipCode;
    private Integer datalevel;
    private String trackingNumber;
    private String courierCompany;
    private Long totalIntegral;
    private List<ShopOrderDetail> orderDetails;
}
