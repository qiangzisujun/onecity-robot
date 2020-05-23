package com.tangchao.shop.service;

import com.tangchao.shop.pojo.UserConf;

import java.util.List;

public interface ConfService {

    /**
     * 查询热搜关键字
     *
     * @return
     */
    List<String> selectHotSearchKey();

    double payAvailableScore(double money);

    UserConf selectCmsValue(String type);
}
