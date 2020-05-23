package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "cms_advert_group")
@Data
public class AdvertGroup {


    //  主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  广告组名称（中文）
    private String groupName;

    //  广告组名称（英文）
    private String groupNameUs;

    //  广告组描述
    private String groupDescribe;

    //  广告组标识
    private String groupCode;

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
    private Date lastModifyTime;

    @Transient
    private String lastModifyName;
}
