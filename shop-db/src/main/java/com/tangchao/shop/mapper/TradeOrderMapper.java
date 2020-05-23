package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.adminDTO.OrderDTO;
import com.tangchao.shop.pojo.TradeOrder;
import com.tangchao.shop.vo.adminVo.TradeOrderVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


public interface TradeOrderMapper extends Mapper<TradeOrder> {
    Integer selectToday();

    Integer selectYesterday();

    Integer selectUnDeliverGoodsNum();

    Integer selectAgentOrderToday();

    Integer selectAgentOrderYesterday();

    Integer selectUnCheckAgentOrderNum();

    Integer selectCustomerToday();

    Integer selectCustomerYesterday();

    Double selectTodayScoreIncome();

    Double selectTodayScoreExpenditure();

    Double selectTodayAmountRecharge();

    Double selectTodayAmountConsumption();

    Integer selectTodayShowNum();

    Integer selectTodayCommentNum();


    /**
     * 根据条件查询订单列表
     * @param orderDTO 查询条件
     * @return List<TradeOrder>
     */
    List<TradeOrderVO> selectOrderList(OrderDTO orderDTO);

    /**
     * 查询统计订单总额
     * @param orderDTO 查询条件
     * @return Map<String, Double>
     */
    Map<String, Object> selectCountOrderTotal(OrderDTO orderDTO);

    int getOrderSum(OrderDTO orderDTO);

    /**
     * 修改超时订单的状态
     * @param minute 超时时间,单位：分钟
     * @param status 未付款状态
     * @return 受影响行数
     */
    int updateOrderStatusByOverTime(@Param(value = "minute") Long minute, @Param(value = "status") Integer status,
                                    @Param(value = "_status") Integer _status);

    /**
     * 修改指定用户超时订单的状态
     * @param status 未付款状态
     * @return 受影响行数
     */
    int updateUserOrderStatusByOverTime(@Param(value = "purchaseCode") Long purchaseCode, @Param(value = "status") Integer status,
                                        @Param(value = "_status") Integer _status);

    /**
     * 个人订单统计
     * @param userCode
     * @return
     */
    int countTradeOrderByUserCode(@Param("userCode") Long userCode);
}
