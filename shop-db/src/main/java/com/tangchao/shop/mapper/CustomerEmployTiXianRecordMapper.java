package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.CustomerEmployTiXianRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface CustomerEmployTiXianRecordMapper extends Mapper<CustomerEmployTiXianRecord> {

    @Select("SELECT SUM(withdraw_price) FROM customer_employ_tixian_record WHERE customer_code=#{customerCode} AND state=#{state}")
    Double findEmploySumByState(@Param("customerCode") Long customerCode, @Param("state") Integer state);
}
