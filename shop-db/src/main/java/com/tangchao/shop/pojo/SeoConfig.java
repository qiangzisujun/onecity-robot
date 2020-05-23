package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 11:15
 */
@Table(name = "cms_seo_config")
@Data
public class SeoConfig implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  关键字
    private String siteKeywords;

    //  描述
    private String siteDescription;

    //  网站标题
    private String siteTitle;

    //  网站分享标题
    private String siteShareTitle;

    //  网站Logo
    private String siteLogo;

    //  域名
    private String siteDomain;

    //  路径
    private String sitePageUri;

    //  是否默认｛0:默认，1：非默认｝
    private Integer isDefault;

    //  标识｛0：正常，-1：删除｝
    private Integer flag;

    //  创建人Id
    private Long create_id;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;
}
