package com.tangchao.shop.params;

import lombok.Data;

/**
 * @Class DeliveryParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/11 11:15
 * @Version 1.0
 **/
@Data
public class DeliveryParam {

    private String orderId;

    private String courierCompany;

    private String trackingNumber;

}
