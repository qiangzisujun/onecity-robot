package com.tangchao.shop.pojo;

import com.tangchao.shop.beans.TreeEntity;
import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Table(name = "sys_menu")
@Data
public class Menu implements TreeEntity<Menu> {

    //  主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  菜单名称
    private String menuName;

    //  菜单名称（英文）
    private String menuNameUs;

    //  菜单层级
    private Integer menuLayer;

    //  排序
    private Integer menuSort;

    //  菜单图标
    private String menuIcon;

    //  链接
    private String menuUri;

    //  父级菜单Id
    private Long menuPid;

    //  菜单下的程序Id，以“，”分割
    private String programIds;

    //  标识｛0：正常，-1：删除｝
    private Integer flag;

    //  创建人Id
    private Long createId;

    //  创建时间
    private Date createTime;

    //  最后修改人Id
    private Long lastModifyId;

    //  最后修改时间
    private Date lastModifyTime;

    /* -------------------------------------------------------------------------------------------------------------- */

    @Transient
    //  旧的排序字段
    private Integer oldSort;

    //  子菜单
    @Transient
    private List<Menu> subMenuList = new ArrayList<>(0);

    //  程序列表
    @Transient
    private List<Program> programList = new ArrayList<>(0);

    @Override
    public Long findId() {
        return this.getId();
    }

    @Override
    public Long findPid() {
        return this.getMenuPid();
    }

    @Override
    public Integer findSort() {
        return this.getMenuSort();
    }

    @Override
    public void setSubList(List<Menu> menuList) {
        this.subMenuList = menuList;
    }
}
