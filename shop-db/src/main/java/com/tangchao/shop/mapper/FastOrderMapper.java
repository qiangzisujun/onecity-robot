package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.adminDTO.OrderDTO;
import com.tangchao.shop.pojo.FastOrder;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/2 16:23
 */
public interface FastOrderMapper extends Mapper<FastOrder> {

    List<Map<String,Object>> selectFastOrderList(OrderDTO orderDTO);


    int getFastOrderSum(OrderDTO orderDTO);

    Map<String,Object> selectPriceTotal(OrderDTO orderDTO);

}
