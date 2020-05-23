package com.tangchao.shop.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "cms_annc")
@Data
public class Annc implements Serializable {


    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 标识｛0：正常，-1：删除｝
    private Integer flag;

    // 创建者
    private Long createId;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 修改者
    private Long lastModifyId;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastModifyTime;

    // 是否显示 {0：不显示，1：显示}
    private Integer isShow;

    // 公告标题
    private String title;

    // 公告内容
    private String content;
}