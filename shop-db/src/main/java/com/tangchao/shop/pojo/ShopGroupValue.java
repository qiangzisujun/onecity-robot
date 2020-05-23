package com.tangchao.shop.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/9 15:17
 */
@Data
@Table(name = "shop_group_value")
public class ShopGroupValue {

    private Long id;
    private Long groupId;
    private String groupValue;
    private Date createTime;
    private Integer status;
}
