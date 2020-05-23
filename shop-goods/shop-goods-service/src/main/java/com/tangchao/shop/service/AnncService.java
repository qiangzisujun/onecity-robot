package com.tangchao.shop.service;


import com.tangchao.shop.pojo.Annc;

import java.util.Date;
import java.util.List;

public interface AnncService {

    /**
     * 查询最新公告
     *
     * @return Annc
     */
    Annc findNewestAnnc();

    Annc findUserNewestAnnc();

    /**
     * 查询历史公告
     *
     * @return List<Annc>
     */
    List<Annc> selectHistoryAnnc();
}
