package com.tangchao.shop.mapper;

import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.CustomerRechargeRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomerRechargeRecordMapper extends Mapper<CustomerRechargeRecord> {

    @Select("SELECT COALESCE(SUM(amount) ,0) FROM customer_recharge_record where customer_code=#{customerCode} and type=#{type}")
    Double findAmountSum(@Param("customerCode") Long customerCode, @Param("type") Integer type);

    List<CustomerRechargeRecord> findCustomerRechargeRecord(CustomerDTO customerDTO);

    int countCustomerRechargeRecord(CustomerDTO customerDTO);

    List<Map<String,Object>> sumCustomerRechargeRecordTotal(@Param("list") List<Long> list);

    List<CustomerRechargeRecord> findAgentRechargeRecord(CustomerDTO customerDTO);

    Double sumCustomerRechargeRecord(CustomerDTO customerDTO);

    int sumAgentRechargeRecord(CustomerDTO customerDTO);

    Double sumProxyExpensesRecord(CustomerDTO customerDTO);

    double withdrawPriceTotal(CustomerDTO customerDTO);

    Double sumCustomerRechargeByGoodsType();
}
