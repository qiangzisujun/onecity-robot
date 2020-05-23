package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.pojo.ShopGoodsType;

import java.util.List;

public interface ShopGoodsTypeService {

    List<ShopGoodsType> goodsTypeList();

    void addGoodsType(String typeName, Long typePid, Long userId);

    void updateGoodsType(ShopGoodsType goodsType);

    void updateSort(Long id_1, Long id_2);

    void deleteGoodsType(Long id);

    PageResult<ShopGoods> getUserCouponList(Integer pageNo, Integer pageSize, Integer typeId);

}
