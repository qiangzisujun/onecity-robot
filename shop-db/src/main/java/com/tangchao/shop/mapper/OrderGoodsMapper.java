package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.OrderGoods;
import com.tangchao.shop.vo.UserByRecordVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderGoodsMapper extends Mapper<OrderGoods> {


    List<UserByRecordVO> selectBuyRecordList(@Param("userCode") Long userCode);

    List<UserByRecordVO> selectUserObtainGoodsList(@Param("userCode") Long userCode);

    /**
     * 按订单编号查询订单商品
     * @param orderNo
     * @return
     */
    List<OrderGoods> selectOrderGoodsByOrderNo(@Param("orderNoList") List<String> orderNo);
}
