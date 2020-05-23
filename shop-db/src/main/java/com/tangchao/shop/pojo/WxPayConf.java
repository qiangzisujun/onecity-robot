package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 11:01
 */
@Table(name = "wxpay_conf")
@Data
public class WxPayConf {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 标记｛-1：删除，0：正常｝
    private Integer flag;

    // 启用标记 { 1：启用，0：未启用 }
    private Integer isOpen;

    // 配置名称
    private String confName;

    // AppID
    private String appId;

    // AppSecret
    private String appSecret;

    // MchID
    private String mchId;

    // KEY
    private String payKey;

    private String protypeAppId;
    private String protypeAppSecret;
}
