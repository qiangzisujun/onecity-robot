package com.tangchao.shop.service;

import com.tangchao.shop.vo.OrderVO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface OrderService {

    /**
     * 创建订单
     *
     * @param map
     * @param request
     * @return
     */
    OrderVO createOrder(Long userCode,Map<String,Object> map, HttpServletRequest request) throws IOException;

    /**
     * 订单支付
     *
     * @param orderNo
     * @param isAutoBuyNext
     * @return
     */
    Double commitOrder(Integer isUse,Long orderNo, Integer isAutoBuyNext, HttpServletRequest request) throws Exception;
}
