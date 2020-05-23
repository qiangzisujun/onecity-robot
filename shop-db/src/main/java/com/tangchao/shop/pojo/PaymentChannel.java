package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/4/17 10:27
 */
@Table(name = "payment_channel")
@Data
public class PaymentChannel {

    private Long id;
    private String mchid;
    private String returnUrl;
    private String urlPrefix;
    private String keyName;
    private String orderNo;
    private Date createTime;
}
