package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShopSpecification;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/3 16:03
 */
public interface ShopSpecificationMapper extends Mapper<ShopSpecification> {

    int deleteShopSpecification(@Param("list") List<Long> list);
}
