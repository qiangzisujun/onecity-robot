package com.tangchao.shop.service;

import com.tangchao.shop.pojo.GoodsType;

import java.util.List;

public interface GoodsTypeService {
    List<GoodsType> goodsTypeList();

    void addGoodsType(String  typeName,Long  typePid,Long userId,String typeNameCN,String typeNameMa);

    void updateGoodsType(GoodsType goodsType);

    void updateSort(Long id_1, Long id_2);

    void delectGoodsType(Long id);
}
