package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * title: SmsType
 * package: com.chichao.eyyg.pojo.entitys.message
 * description: 短信类型-数据库实体类
 * author: 王飞腾
 * date: 2018/7/12
 */
@Table(name = "third_sms_type")
@Data
public class SmsType implements Serializable {

    //  主键Id,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  短信类型名称（中文）
    private String smsTypeName;

    //  短信类型名称（英文）
    private String smsTypeNameUs;

    //  短信类型的唯一标识
    private String smsTypeCode;

    //  标记{ 0：正常，-1：删除 }
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

    //  模板列表
    @Transient
    private List<SmsTemplate> templateList = new ArrayList<>(0);
}
