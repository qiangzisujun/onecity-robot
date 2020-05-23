package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.ShopCategory;
import com.tangchao.shop.vo.ShopCategoryVO;

import java.util.List;

public interface ShopCategoryService {

    /**
     * 获取分类
     *
     * @return
     */
    PageResult<ShopCategory> getShopCategoryListByPage();

    List<ShopCategoryVO> getShopCategoryList(Long cateId);
}
