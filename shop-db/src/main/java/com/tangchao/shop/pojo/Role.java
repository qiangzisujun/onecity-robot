package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 11:34
 */

@Data
@Table(name = "sys_role")
public class Role implements Serializable {
    //  主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    //  角色名称
    private String roleName;

    //  角色名称（英文）
    private String roleNameUs;

    //  角色可操作的程序Id，以“，”分割
    private String programIds;

    //  用户可操作的菜单Id，已“，”分割
    private String menuIds;

    //  角色状态｛0：正常，-1：删除｝
    private Integer roleStatus;

    //  创建人Id
    private Long createId;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

    @Transient
    private List<Program> programList = new ArrayList<>(0);

    @Transient
    private List<Menu> menuList = new ArrayList<>(0);
}
