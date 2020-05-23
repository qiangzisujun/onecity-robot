package com.tangchao.shop.mapper;


import com.tangchao.shop.pojo.CustomerAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.BaseMapper;

public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {

    @Update("UPDATE customer_address SET is_default=0 WHERE customer_code=#{customerCode}")
    int cancelDefaultAddress(@Param("customerCode") Long customerCode);
}
