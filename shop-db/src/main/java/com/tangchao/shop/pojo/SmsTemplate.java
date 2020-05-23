package com.tangchao.shop.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * title: SmsTemplate
 * package: com.chichao.eyyg.pojo.entitys.message
 * description: 短信模板-数据库实体类
 * author: 王飞腾
 * date: 2018/7/12
 */
@Table(name = "third_sms_template")
@Data
public class SmsTemplate implements Serializable {

    //  主键Id,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  短信签名
    private String smsSignName;

    //  短信内容
    private String smsContent;

    //  短信类型的唯一标识
    private String smsTypeCode;

    //  标记{ 0：未启用,1：已启用，-1：删除 }
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

    /* -------------------------------------------------------------------------------------------------------------- */

    //  短信类型中文名称
    @Transient
    private String smsTypeName;

    @Transient
    private String smsTypeNameUs;

    //  手机号码
    @Transient
    private String mobile;
}
