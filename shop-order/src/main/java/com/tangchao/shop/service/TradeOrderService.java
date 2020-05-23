package com.tangchao.shop.service;

import com.tangchao.shop.dto.TradeOrderDTO;
import com.tangchao.shop.dto.adminDTO.OrderDTO;
import com.tangchao.shop.pojo.ExpressForm;
import com.tangchao.shop.pojo.GoodsRobot;
import com.tangchao.shop.pojo.OrderGoods;
import com.tangchao.shop.pojo.TradeOrder;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface TradeOrderService {


   Map OrderManagement();

   /**
    *
    * @param userId
    * @param orderDTO
    * @return
    */
   Map<String,Object> selectOrderTradeList(Long userId, OrderDTO orderDTO);

   TradeOrder getOrderDetailInfo(Long userId, String orderNo);

   void setLuckyUser(Long userId, String stageId,String userCode);

   void closeLuckyUser(Long userId, String stageId, String userCode);

   Map<String,Object> getFastOrderList(Long userId, OrderDTO orderDTO);

    void autoCompleteCode(Long userId, Map<String, Object> data);

   Map<String,Object> getWinningOrderList(Long userId, OrderDTO orderDTO);

   Map<String,Object> countOrderTradeList(Long userId, OrderDTO orderDTO);

   Map<String,Object> countOrderList(Long userId, OrderDTO orderDTO);

   Map<String,Object> WinningOrderInfo(Long userId, String orderOrder);

   void updateWinningOrderInfo(Long userId, Map<String, Object> data);


   ExpressForm orderShipTemplate(Long userId);

   void updateExpressFormTemplate(Long userId, ExpressForm expressForm);

   void updateOverTimeOrderStatus(Long overTime);

   void exportWinningOrder(Long userId,String goodsNo,String orderNo,String userName,String userMobile,String createStartTime,String createEndTime,Integer status,HttpServletResponse response) throws UnsupportedEncodingException;

   Map<String,Object> winningOrderByGoodsNo(Integer pageNo, Integer pageSize, String goodsNo);

   int robotPayGoods(TradeOrderDTO tradeOrder, GoodsRobot goodsRobot) throws Exception;

   int robotOrCustomerPayGoods(List<OrderGoods> orderGoodsList, Integer IsRobot, GoodsRobot goodsRobot) throws Exception;

   void proxyCompleteCode(Long userId,String checkCode,Long id);
}
