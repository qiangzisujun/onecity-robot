package com.tangchao.user.service;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.pojo.WxPayConf;

import java.util.List;
import java.util.Map;

public interface CmsConfigService {


    Map<String,Object> consultation();


    UserConf selectCmsValue(String type);

    Map<String,Object> getCmsInfo(Long userId);

    PageInfo getCmsSetUpInfo(Long userId,Integer pageNo,Integer pageSize);

    void updateCmsSetUpInfo(Long userId, UserConf conf);

    void deleteCmsSetUpInfo(Long userId, Map<String,Object> data);

    WxPayConf wxPayInfo(Long userId);

    void updateWxPayInfo(Long userId, WxPayConf conf);

    PageInfo getSeoConfigList(Long userId);

    void updateCmsInfo(Long userId, List<Map<String, Object>> maps);

    void insertCmsSetUpInfo(Long userId, UserConf conf);

    String getMallSwitch();
}
