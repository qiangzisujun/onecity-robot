package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.pojo.Lottery;
import com.tangchao.shop.pojo.WinningOrder;

import java.util.Map;

public interface WinningOrderService {


    Map<String,Object> prizeWinning(Long userCode, Integer pageNo, Integer pageSize);

    /**
     * 创建中奖订单
     * @param lottery 中奖信息
     */
    void createOrder(Lottery lottery, GoodsStage goodsStage);

    String getCheckCode(Long userCode, String winOrderId);

    void delivery(Long userCode, String orderNo);
}
