package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * title: Program
 * package: com.chichao.eyyg.pojo.entitys.sys
 * description: 系统程序
 * author: 王飞腾
 * date: 2018/6/25
 */
@Table(name = "sys_program")
@Data
public class Program{

    //  主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    //  程序名称
    private String programName;

    //  程序名称（英文）
    private String programNameUs;

    //  程序编码
    private String programCode;

    //  程序图标
    private String programIcon;

    //  标记｛0：正常，-1：删除｝
    private Integer flag;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

    /* -------------------------------------------------------------------------------------------------------------- */
}
