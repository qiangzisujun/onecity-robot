package com.tangchao.shop.pojo;

import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/12 20:39
 */
@Data
public class NotifyApi {

    private Integer Status;
    private String Mch_id;
    private String Out_trade_no;
    private String Total_fee;
    private String Pay_type;
    private String Sign;
}
