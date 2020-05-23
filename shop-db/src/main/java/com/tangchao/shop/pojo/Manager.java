package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 11:21
 */
@Data
@Table(name = "sys_manager")
public class Manager implements Serializable {


    //  用户Id，主键
    @Id
    private Long id;

    //  用户名
    private String userName;

    //  用户密码
    private String userPwd;

    //  用户手机号
    private String userMobile;

    //  用户邮箱
    private String userEmail;

    //  用户真实姓名
    private String userRealname;

    //  用户头像
    private String userPortrait;

    //  状态标记｛0：正常，-1：已删除｝
    private Integer flag;

    //  角色Id
    private String roleId;

    //  创建人Id
    private Long createId;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;
}
