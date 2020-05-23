package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class UnifiedOrderResponse {

    private String nonce_str;

    private String device_info;

    private String appid;

    private String sign;

    private String err_code;

    private String trade_type;

    private String return_msg;

    private String result_code;

    private String err_code_des;

    private String mch_id;

    private String return_code;

    private String prepay_id;

    private String timeSign;
}
