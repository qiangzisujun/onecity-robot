package com.tangchao.shop.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * title: SmsInterface
 * package: com.chichao.eyyg.pojo.entitys.message
 * description: 短信接口-数据库实体类
 * author: 王飞腾
 * date: 2018/7/12
 */
@Table(name = "third_sms_interface")
@Data
public class SmsInterface implements Serializable {

    //  主键Id,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  接口访问密钥Id（用户名）
    private String accessKeyId;

    //  接口的访问密钥（密码）
    private String accessKeySecret;

    //  接口供应商名称
    private String supplierName;

    //  服务地址
    private String serverHost;

    //  标记{ 0：未启动，1：已启用，-1：删除 }
    private Integer flag;

    //  创建人Id
    private Long createId;

    //  创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifyTime;
}
