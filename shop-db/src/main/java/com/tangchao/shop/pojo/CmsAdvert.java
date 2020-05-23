package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "cms_advert")
@Data
public class CmsAdvert {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  广告描述（中文）
    private String adDescribe;

    //  广告描述（英文）
    private String adDescribeUs;

    //  广告链接
    private String adHref;

    //  广告图
    private String adImg;

    //  广告唯一标识
    private String adCode;

    //  背景色
    private String adBackgroundColor;

    //  排序值
    private Integer adSort;

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

    /* -------------------------------------------------------------------------------------------------------------- */

    //  中文分组名称
    @Transient
    private String groupName;
    //  英文分组名称
    @Transient
    private String groupNameUs;

    /* -------------------------------------------------------------------------------------------------------------- */

}
