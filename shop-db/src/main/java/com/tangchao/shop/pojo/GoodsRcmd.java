package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "goods_rcmd")
@Data
public class GoodsRcmd implements Serializable {

    // 主键,自增
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

    // 商品信息表（goods_info）
    private Long goodsId;

    // 商品热度{0~999}，越小热度越高
    private Integer goodsHot;
}
