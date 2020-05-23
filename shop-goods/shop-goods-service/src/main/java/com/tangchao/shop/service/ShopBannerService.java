package com.tangchao.shop.service;


import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.ShopBanners;


public interface ShopBannerService {

    /**
     * 获取首页广告
     *
     * @return
     */
    PageResult<ShopBanners> getShopBanners();
}
