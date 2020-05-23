package com.tangchao.shop.service;

import com.tangchao.shop.pojo.GoodsStage;

import java.util.List;

public interface OpenWinningService {

    /**
     * 查询开奖时间
     *
     * @return （商品满团后与开奖的时间间隔：单位秒，最小值5秒）
     */
    Integer findOpenWinningTime();

    List<GoodsStage> findWaitOpenWinning(Integer isActivity);
}
