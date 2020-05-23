package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.CustomerDto;
import com.tangchao.shop.dto.adminDTO.OrderDTO;
import com.tangchao.shop.pojo.WinningOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WinningOrderMapper extends Mapper<WinningOrder> {

    /**
     * 根据用户code查询中奖金额
     * @param code
     * @return
     */
    @Select("select COALESCE(SUM(goods_price),0) from winning_order where customer_code = #{code}")
    Double winningPriceTotalByUserCode(@Param("code") Long code);

    List<WinningOrder> selectWinningOrderList(OrderDTO  orderDTO);

    int countWinningOrderList(OrderDTO  orderDTO);

    Map<String, Object> getWinningPriceTotal(OrderDTO  orderDTO);

    List<Map<String,Object>> sumWinningPriceTotalByUserCode(@Param("list") List<Long> list);

    WinningOrder getWinningOrderByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询代理订单记录
     * @param customerDto
     * @return
     */
    List<Map<String, Object>> selectProxyOrderList(CustomerDto customerDto);

    /**
     * 查询代理订单金额统计
     * @param customerDto
     * @return
     */
    Double selectProxyOrderCount(CustomerDto customerDto);


    int countWinningOrderByUserCode(@Param("userCode") Long userCode);

    List<Map<String,Object>> findWinningOrderByPojo(OrderDTO  orderDTO);

    List<Map<String, Object>> selectWinningOrderByGoodsNo(@Param("goodsNo") String goodsNo);

    int countWinningOrderByGoodsNo(@Param("goodsNo") String goodsNo);
}
