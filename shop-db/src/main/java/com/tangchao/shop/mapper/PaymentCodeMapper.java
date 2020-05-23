package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.OrderGoods;
import com.tangchao.shop.pojo.PaymentCode;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PaymentCodeMapper extends Mapper<PaymentCode> {

    PaymentCode callPaymentCode(@Param("type") String type, @Param("price") Double price);

    int updatePaymentCodeByPrimaryKeySelective(PaymentCode paymentCode);

    List<String> getPaymentCodeList();
}
