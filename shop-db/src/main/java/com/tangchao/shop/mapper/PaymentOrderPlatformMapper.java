package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.PaymentOrderPlatform;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/12/10 21:10
 */
public interface PaymentOrderPlatformMapper extends Mapper<PaymentOrderPlatform> {

    @Update("update payment_order_platform set payment_status=2,platform_trade_no=#{platformTradeNo} where id=#{id}")
    int updatePlatformTradeNo(@Param("platformTradeNo") String platformTradeNo,@Param("id") Long id);
}
