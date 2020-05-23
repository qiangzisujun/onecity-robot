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
 * @Date 2019/11/14 19:34
 */
// 主键
@Table(name = "cms_guide")
@Data
public class Guide implements Serializable {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 标识｛0：正常，-1：删除｝
    private Integer flag;

    // 创建者
    private Long createId;

    // 创建时间
    private Date createTime;

    // 修改者
    private Long lastModifyId;

    // 更新时间
    private Date lastModifyTime;

    // 指南标题
    private String title;

    // 指南内容
    private String content;
}
