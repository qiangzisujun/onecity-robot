package com.tangchao.shop.service;

import com.tangchao.shop.vo.BannerAndCategory;

import java.util.List;
import java.util.Map;

public interface IndexService {

    BannerAndCategory getBannerAndCategoryInfo();

    /**
     * 元购商城
     *
     * @return
     */
    List<Map<String, Object>> getBannerList();

    List<Map<String, Object>> getNavigationList();

    Map<String, Object> getWebSiteStatus();

    Map<String, Object> getDownloadAPPURL();

    List<Map<String, Object>> getZSBannerList();

    List<Map<String,Object>> showZSBannerList(String key);
}
