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
 * @Date 2019/11/11 11:40
 */
@Table(name = "cms_mail_conf")
@Data
public class MailConf implements Serializable {

    //  主键,自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  发送人名称
    private String sendName;

    //  邮箱账号
    private String smtpAccount;

    //  邮箱密码
    private String smtpPassword;

    //  邮箱服务器地址
    private String smtpHost;

    //  端口
    private Integer smtpPort;

    //  标记{ 0：正常，-1：删除 }
    private Integer flag;

    //  创建人Id
    private Long createId;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

}
