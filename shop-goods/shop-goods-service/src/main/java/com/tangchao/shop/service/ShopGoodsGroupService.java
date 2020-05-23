package com.tangchao.shop.service;

import com.tangchao.shop.params.ShopSpecGroupParam;
import com.tangchao.shop.pojo.ShopSpecGroup;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/3/9 10:48
 */
public interface ShopGoodsGroupService {

    /**
     * 新增规格组
     * @param group
     */
    void addGoodsGroup(ShopSpecGroup group);

    void updateGoodsGroup(ShopSpecGroup group);

    void deleteGoodsType(Long id);

    List<ShopSpecGroup> selectGoodsGroup(Long userId);
}
