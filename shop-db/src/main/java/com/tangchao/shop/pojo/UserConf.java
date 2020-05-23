package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "cms_conf")
@Data
public class UserConf implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  配置Key
    private String confKey;

    //  配置名称
    private String confName;

    //  配置值
    private String confValue;

    //  数据类型{ 0：普通文本，1：图片地址 }
    private Integer dataType;

    //  配置描述
    private String confDescribe;

    //  标记｛-1：删除，0：正常｝
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