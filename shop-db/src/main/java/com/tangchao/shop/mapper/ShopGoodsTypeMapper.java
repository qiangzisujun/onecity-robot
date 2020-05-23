package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShopGoodsType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ShopGoodsTypeMapper extends Mapper<ShopGoodsType> {

    Integer selectMaxSortByPid(Long pid);

    Long selectPidById(Long id);

    List<Long> selectSubIdById(Long id);

    Integer selectSortById(Long id);

    List<Long> selectSubIdsByIds(String ids);
}
