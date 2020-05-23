package com.tangchao.shop.pojo;

import lombok.Data;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/16 20:45
 */
@Data
public class CPNPApi {

    private String resultCode;//状态
    private String message;//文本信息
    private String orderNo;//客户订单号
    private String sysOrderNo;//系统订单号
    private String threeOrderNo;//支付订单号
    private String price;//订单金额
    private String payPrice;//用户支付金额
    private String sign;//签名
}
