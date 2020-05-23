package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShopGoods;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ShopGoodsMapper extends Mapper<ShopGoods> {
    List<Map<String, Object>> findAll();
}
