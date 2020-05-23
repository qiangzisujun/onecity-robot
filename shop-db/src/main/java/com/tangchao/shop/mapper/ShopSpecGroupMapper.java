package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShopSpecGroup;
import tk.mybatis.mapper.common.Mapper;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/6 16:36
 */
public interface ShopSpecGroupMapper extends Mapper<ShopSpecGroup> {

    int insertSelectiveGroup(ShopSpecGroup group);
}
