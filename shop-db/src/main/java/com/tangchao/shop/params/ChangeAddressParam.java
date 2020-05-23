package com.tangchao.shop.params;

import lombok.Data;

/**
 * @Class ChangeAddressParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/11 11:40
 * @Version 1.0
 **/
@Data
public class ChangeAddressParam {

    private String orderId;

    private String userName;

    private String userMobile;

    private String receiverAddress;

    private String zipCode;

}
