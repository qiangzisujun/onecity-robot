package com.tangchao.shop.params;

import com.tangchao.shop.pojo.ShopGroupValue;
import lombok.Data;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/9 16:11
 */
@Data
public class ShopSpecGroupParam {

    private Long id;
    private String name;
    private Integer status;
    private Long typeId;
    private Date createTime;
    private String typeName;

    @Transient
    private List<ShopGroupValue> shopGroupValues;
}
