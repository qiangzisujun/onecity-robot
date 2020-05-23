package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/6 16:34
 */
@Data
@Table(name = "shop_spec_group")
public class ShopSpecGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer status;
    private Long typeId;
    private Date createTime;
    private String typeName;

    @Transient
    private List<ShopGroupValue> shopGroupValues;


}
